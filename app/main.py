from model.models import *
from logic import process
import json
import os
import sys
import uvicorn

from optparse import Option
from datetime import datetime, timedelta
from typing import Optional, Union, List
from functools import reduce
from pytz import timezone
from fastapi import FastAPI, Request, Response, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import create_engine, Boolean, Column, ForeignKey, Integer, String, Date, desc
from sqlalchemy.orm import relationship, declarative_base, Session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base


SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(SCRIPT_DIR))


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
SECRET_FILE = os.path.join(BASE_DIR, "secrets.json")
secrets = json.loads(open(SECRET_FILE, "r", encoding="utf-8-sig").read())

DB = secrets["db"]
DB_URL = f"mysql+pymysql://{DB['user']}:{DB['localpassword']}@{DB['localhost']}:{DB['port']}/{DB['database']}?charset=utf8mb4"

engine = create_engine(
    DB_URL
)
Base = declarative_base()


SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
session = SessionLocal()

# Base.metadata.create_all(bind=engine)
app = FastAPI()

# Dependency


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def get_score_and_strength(person_id: str):
    ws = session.query(WalkScore).filter(
        WalkScore.person_id == person_id).first()
    user = session.query(User).filter(User.person_id == person_id).first()
    return {"score": ws, "strength": user.strength}


@app.get("/")
def read_root():
    return {"data": "Hello World!"}


def getProphetResult(raw_data, score_and_strength, isApplyScore):
    input_data = [[data.get_date(), data.get_steps()] for data in raw_data]
    return process.applyProphet(input_data, score_and_strength, isApplyScore)


def getStepsLastNDays(person_id, amount):
    today = datetime.now(timezone('Asia/Seoul'))

    result = session.query(WalkCount).filter(
        WalkCount.person_id == person_id).filter(
        WalkCount.walk_date >= (today - timedelta(days=amount+1))
    ).filter(
        WalkCount.walk_date < today
    ).all()

    return result


def getStepsBetween(person_id, start, end):
    today = datetime.now(timezone('Asia/Seoul'))
    start = datetime.strptime(start, "%Y-%m-%d")
    end = datetime.strptime(end, "%Y-%m-%d")

    result = session.query(WalkCount).filter(
        WalkCount.person_id == person_id).filter(
        WalkCount.walk_date >= start).filter(
        WalkCount.walk_date <= end).all()
    return result


def save_walkcount(_person_id: str, _walk_date, _steps):
    db_walkcount = WalkCount(person_id=_person_id,
                             walk_date=_walk_date,
                             steps=_steps)

    wc = session.query(WalkCount).filter(
        WalkCount.person_id == _person_id).filter(
        WalkCount.walk_date == _walk_date).first()
    if(wc is not None):
        session.delete(wc)
        session.commit()
    session.add(db_walkcount)
    session.commit()
    session.refresh(db_walkcount)
    return db_walkcount


@app.get("/walkscore/{person_id}")
def get_walkscore(person_id: str):
    ws = session.query(WalkScore).filter(
        WalkScore.person_id == person_id).first()
    return {"data": ws, "ResultCode": 200}


@app.post("/walkscore")
def save_walkscore(walkscore: WalkScoreBase):
    db_walkscore = WalkScore(person_id=walkscore.person_id,
                             mon=walkscore.mon,
                             tue=walkscore.tue,
                             wed=walkscore.wed,
                             thu=walkscore.thu,
                             fri=walkscore.fri,
                             sat=walkscore.sat,
                             sun=walkscore.sun)

    ws = session.query(WalkScore).filter(
        WalkScore.person_id == walkscore.person_id).first()
    if(ws is not None):
        session.delete(ws)
        session.commit()
    session.add(db_walkscore)
    session.commit()
    session.refresh(db_walkscore)
    return {"ResultCode": 200}


@ app.post("/login", status_code=200)
async def login(logindata: LoginData):
    result = {
        "person_id": logindata.person_id,
        "nickname": "",
        "ResultCode": 500
    }
    user = session.query(User).filter(
        User.person_id == logindata.person_id).filter(
        User.password == logindata.password).first()

    if(user is None):
        message = "Fail"
    else:
        message = "Success"
        result["ResultCode"] = 200
        result["nickname"] = user.nickname
    return result

def get_grade(percent):
    percent *= 100
    if (percent <= 4):
      return "DIAMOND"
    elif (percent <= 11):
      return "PLATINUM"
    elif (percent <= 23):
      return "GOLD"
    elif (percent <= 40):
      return "SILVER"
    elif (percent <= 60):
      return "BRONZE"
    else:
      return "UNRANKED"


@app.post("/rank")
async def get_group_rank(groupRank: GroupRank):
    current_user = session.query(User).filter(User.person_id == groupRank.person_id).first()
    total_list = []
    other_group_users = session.query(User).filter(
      User.age >= (groupRank.age // 10) * 10).filter(
      User.age < (groupRank.age // 10) * 10 + 10).filter(
      User.gender == groupRank.gender.upper()).all()

    result = {
      "code": 200,
      "rank": []
    }
    for user in other_group_users:
      # 30일간 전체 걸음 수
      stepsOfWeek = getStepsLastNDays(user.person_id, 30)
      total_steps = reduce(lambda a, b: a + b, [s.get_steps() for s in stepsOfWeek], 0)
      total_list.append([user.person_id, user.nickname, total_steps])

    total_list.sort(key=lambda x: x[2], reverse=True)

    for idx, item in enumerate(total_list):
      result["rank"].append({
            "rankIdx": idx+1,
            "userNickName": item[1],
            "steps": item[2],
            "grade": get_grade((idx+1) / len(total_list))
          })
    
    if(len(result["rank"]) >= 7):
        result["rank"] = result["rank"][:3] + [result["rank"][len(result["rank"])//2 - 1]] + result["rank"][-3:]
    return result
    


@app.post("/initialrank")
async def get_initial_rank(userId: UserId):
    my_rank = 0
    current_user = session.query(User).filter(User.person_id == userId.person_id).first()
    if(current_user is None):
      print(f"[Error] User {userId.person_id} doesnt exist")
      return {"code": 404}

    result = {
      "code": 200,
      "rank": [],
      "userGroupRank": "",
      "allUserRank": ""
    }
    nickname_total_list = []
    nickname_total_list_group = []

    all_users = session.query(User).all()
    group_users = session.query(User).filter(
      User.age >= (current_user.age // 10) * 10).filter(
      User.age < (current_user.age // 10) * 10 + 10).filter(
      User.gender == current_user.gender).all()

    # 지난주 걸음수 합계를 계산한 결과가 필요함
    for user in all_users:
        # 30일간 전체 걸음 수
        stepsOfWeek = getStepsLastNDays(user.person_id, 30)
        total_steps = reduce(lambda a, b: a + b, [s.get_steps() for s in stepsOfWeek], 0)
        nickname_total_list.append([user.person_id, user.nickname, total_steps])

    for user in group_users:
        # 30일간 전체 걸음 수
        stepsOfWeek = getStepsLastNDays(user.person_id, 30)
        total_steps = reduce(lambda a, b: a + b, [s.get_steps() for s in stepsOfWeek], 0)
        nickname_total_list_group.append([user.person_id, user.nickname, total_steps])

    nickname_total_list.sort(key=lambda x: x[2], reverse=True)
    # 전체 유저
    for idx, pair in enumerate(nickname_total_list):
        if(userId.person_id == pair[0]):
          result["allUserRank"] = get_grade((idx+1) / len(nickname_total_list))
          my_rank = idx+1
        
        result["rank"].append({
          "rankIdx": idx+1,
          "userNickName": pair[1],
          "steps": pair[2],
          "grade": get_grade((idx+1) / len(nickname_total_list))
        })

    nickname_total_list_group.sort(key=lambda x: x[2], reverse=True)

    # 그룹 유저
    for idx, pair in enumerate(nickname_total_list_group):
        if(userId.person_id == pair[0]):
          result["userGroupRank"] = get_grade((idx+1) / len(nickname_total_list_group))

    if(len(result["rank"]) >= 7):
      if(my_rank > 3 and my_rank <= len(result["rank"])-3):
        result["rank"] = result["rank"][:3] + [result["rank"][my_rank-1]] + result["rank"][-3:]
      else:
        result["rank"] = result["rank"][:3] + [result["rank"][len(result["rank"])//2 - 1]] + result["rank"][-3:]

    # return json.dumps(result, indent=4)
    return result



@ app.get("/users/{person_id}")
async def get_user_by_id(person_id: str):
    user = session.query(User).filter(User.person_id == person_id).first()
    return user


@app.post("/check/id", status_code=200)
async def isExistUser(userId: UserId):
    resultCode = 200
    isExist = False
    user = session.query(User).filter(
        User.person_id == userId.person_id).first()
    if (user):
        isExist = True
        resultCode = 500
    return {"ResultCode": resultCode}


@app.post("/check/nickname", status_code=200)
async def isExistNickname(userNickName: UserNickName):
    resultCode = 200
    isExist = False
    user = session.query(User).filter(
        User.nickname == userNickName.nickname).first()
    if (user):
        isExist = True
        resultCode = 500
    return {"ResultCode": resultCode}


@ app.post("/join", status_code=200)
async def save_user(user: UserCreate):
    # 추후 암호화
    fake_password = user.password
    db_user = User(person_id=user.person_id,
                   nickname=user.nickname,
                   age=user.age,
                   password=fake_password,
                   gender=user.gender,
                   home_address=user.home_address,
                   comp_address=user.comp_address,
                   strength=user.strength,
                   category=user.category,
                   walk_total_score=user.walk_total_score
                   )
    session.add(db_user)
    session.commit()
    session.refresh(db_user)
    return {"ResultCode": 200}


@ app.get("/{person_id}/rawsteps/recent")
def read_steps(person_id: str):
    result = session.query(WalkCount).filter(
        WalkCount.person_id == person_id).filter(
        WalkCount.walk_date >= datetime.today() - timedelta(days=8)
    ).filter(
        WalkCount.walk_date < datetime.today()
    ).all()
    return result


@ app.get("/{person_id}/rawsteps/last7")
def read_steps(person_id: str):
    result = session.query(WalkCount).filter(
        WalkCount.person_id == person_id).order_by(desc('walk_date')).limit(7).all()
    return result[::-1]


@ app.get("/{person_id}/targetsteps")
def read_steps(person_id: str):
    isApplyScore = True
    steps_dict_list = getStepsLastNDays(person_id, 21)
    score_and_strength = get_score_and_strength(person_id)
    return {"targetSteps": getProphetResult(steps_dict_list, score_and_strength, isApplyScore), "ResultCode": 200}

# test for paper


@ app.get("/test/period/steps")
def read_steps_test_period():
    count = 0
    sum_of_difference = 0
    start = '2022-11-05'
    end = '2022-11-11'
    # periods = [14, 21, 28] # 2주, 3주, 4주
    periods = [21]  # 2주, 3주, 4주

    result = {
        "14": 0,
        "21": 0
        # "28": 0
    }
    person_ids = ["km-m-20-1", "sm-20-m-1",
                  "sm-20-m-2", "sm-20-m-3", "sm-20-m-4"]

    # person_ids = ["km-m-20-1","km-m-20-2","km-f-20-3","km-f-20-4","sm-20-m-1","sm-20-m-2","sm-20-m-3","sm-20-m-4","sh-f-20-1","sh-f-20-2","sh-f-20-3","sh-f-20-4","sh-f-20-5","sh-f-20-6"]
    for period in periods:
        for person_id in person_ids:
            step_sum_without_score = 0
            step_sum_real = 0
            sum_of_difference_ratio = 0

            score_and_strength = get_score_and_strength(person_id)
            # steps_dict_list = getStepsLastNDays(person_id, period)
            end = '2022-11-04'
            start = (datetime.strptime(end, '%Y-%m-%d') -
                     timedelta(days=period)).strftime('%Y-%m-%d')
            steps_dict_list = getStepsBetween(person_id, start, end)
            # 예측점수 미적용한 prophet 걸음수 합계
            result_without_score = getProphetResult(
                steps_dict_list, score_and_strength, False)["mid"]
            for step_data in result_without_score:
                step_sum_without_score += step_data["steps"]
            # print(str(period) + "일 without_score: " +
            #       str(step_sum_without_score))
            # 실제 걸음 수 합
            walkcounts = getStepsBetween(person_id, '2022-11-05', '2022-11-11')
            for wc in walkcounts:
                step_sum_real += wc.get_steps()
            sum_of_difference_ratio += (abs(step_sum_real -
                                            step_sum_without_score) / step_sum_real) * 100
            result[str(period)] += sum_of_difference_ratio
        result[str(period)] = round(result[str(period)] / len(person_ids))

    # 차이 합계
    return {"data": result}

@ app.get("/test/score/steps")
def read_steps_test_score():
    count = 0
    sum_of_difference = 0
    start = '2022-11-05'
    end = '2022-11-11'
    # periods = [14, 21, 28] # 2주, 3주, 4주
    periods = [21]  # 2주, 3주, 4주

    result = {
        "score": 0,
        "non_score": 0
        # " "28": 0"
    }
    person_ids = ["km-m-20-1", "sm-20-m-1",
                  "sm-20-m-2", "sm-20-m-3", "sm-20-m-4"]
    period = 21
    # person_ids = ["km-m-20-1","km-m-20-2","km-f-20-3","km-f-20-4","sm-20-m-1","sm-20-m-2","sm-20-m-3","sm-20-m-4","sh-f-20-1","sh-f-20-2","sh-f-20-3","sh-f-20-4","sh-f-20-5","sh-f-20-6"]

    for person_id in person_ids:
        step_sum_without_score = 0
        step_sum_with_score = 0
        step_sum_real = 0
        sum_of_difference_ratio_without_score = 0
        sum_of_difference_ratio_with_score = 0

        score_and_strength = get_score_and_strength(person_id)
        end = '2022-11-04'
        start = (datetime.strptime(end, '%Y-%m-%d') -
                 timedelta(days=period)).strftime('%Y-%m-%d')
        steps_dict_list = getStepsBetween(person_id, start, end)
        # 예측점수 미적용한 prophet 걸음수 합계
        result_without_score = getProphetResult(
            steps_dict_list, score_and_strength, False)["mid"]

        for step_data in result_without_score:
            result.setdefault(step_data["date"], {
                "predict_real_score": 0,
                "predict_with_score": 0,
                "predict_without_score": 0,
                "predict_ratio_without_score": 0,
                "predict_ratio_with_score": 0
            })

        for step_data in result_without_score:
            result[step_data["date"]]["predict_without_score"] = step_data["steps"]
            step_sum_without_score += step_data["steps"]

        # 예측점수 적용한 prophet 걸음수 합계
        result_with_score = getProphetResult(
            steps_dict_list, score_and_strength, True)["mid"]
        for step_data in result_with_score:
            result[step_data["date"]]["predict_with_score"] = step_data["steps"]
            # print(f'{person_id}: {step_data["date"]} -> {result[step_data["date"]]["predict_with_score"]}')
            step_sum_with_score += step_data["steps"]

        # 실제 걸음 수
        walkcounts = getStepsBetween(person_id, '2022-11-05', '2022-11-11')
        for wc in walkcounts:
            date = wc.get_date()
            real = wc.get_steps()
            # ratio
            m1 = result[wc.get_date()]["predict_without_score"]
            m2 = result[wc.get_date()]["predict_with_score"]
            # method 1
            result[date]["predict_ratio_without_score"] += get_ratio(
                real, m1) / len(person_ids)
            # method 2
            result[date]["predict_ratio_with_score"] += get_ratio(
                real, m2) / len(person_ids)
            step_sum_real += wc.get_steps()
            # print
            # print(f'#{person_id} [{wc.get_date()}]')
            # print(
            #     f'real: {wc.get_steps()}, m1: {result[wc.get_date()]["predict_without_score"]}')
            # print(
            #     f'real: {wc.get_steps()}, m2: {result[wc.get_date()]["predict_with_score"]}')
            # print(get_ratio(real, m1))
            # print(get_ratio(real, m2))

        sum_of_difference_ratio_without_score += (
            abs(step_sum_real-step_sum_without_score) / step_sum_real) * 100
        result["non_score"] += sum_of_difference_ratio_without_score

        sum_of_difference_ratio_with_score += (
            abs(step_sum_real-step_sum_with_score) / step_sum_real) * 100
        result["score"] += sum_of_difference_ratio_with_score

    return {"data": result}


def get_ratio(v1, v2):
    if v1 > v2:
        return 100 * (1 - (v1 - v2) / v1)
    else:
        return 100 * (1 - (v2 - v1) / v2)


@ app.post("/initialData", status_code=200)
async def save_walkcount_days(steps: Steps):
    result = []
    resultCode = 500
    person_id = steps.person_id
    steps_list = steps.dict()["stepsOf3Weeks"]
    for i in range(0, len(steps_list), 2):
        _date = steps_list[i]
        _step = steps_list[i+1]
        result.append(save_walkcount(person_id, _date, _step))
    if result:
        resultCode = 200
    return {"ResultCode": resultCode}




if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
