import json
import os
import sys
import uvicorn

from optparse import Option
from datetime import datetime, timedelta
from typing import Optional, Union, List
from fastapi import FastAPI, Request, Response, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import create_engine, Boolean, Column, ForeignKey, Integer, String, Date
from sqlalchemy.orm import relationship, declarative_base, Session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base


SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(SCRIPT_DIR))

from logic import process
from model.models import *

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
SECRET_FILE = os.path.join(BASE_DIR, "secrets.json")
secrets = json.loads(open(SECRET_FILE, "r", encoding="utf-8-sig").read())

DB = secrets["db"]
DB_URL = f"mysql+pymysql://{DB['user']}:{DB['password']}@{DB['host']}:{DB['port']}/{DB['database']}?charset=utf8mb4"

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


@app.get("/")
def read_root():
    return {"data": "Hello World!"}

def getProphetResult(person_id: str, raw_data):
    input_data = [[data.get_date(), data.get_steps()] for data in raw_data]
    return process.applyProphet(input_data, get_walkscore(person_id))

# 오늘 제외
def getStepsLastNDays(person_id, amount):
    total_walkcount_count = len(session.query(WalkCount).filter(
      WalkCount.person_id == person_id).all())

    if amount > total_walkcount_count:
        amount = total_walkcount_count

    result = session.query(WalkCount).filter(
        WalkCount.person_id == person_id).filter(
        WalkCount.walk_date >= datetime.today() - timedelta(days=amount+1)
    ).filter(
        WalkCount.walk_date < datetime.today()
    ).all()
    return result
    # return list(map(lambda r: r.get_steps(), result))


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
    return ws


@app.post("/walkscore/{person_id}")
def save_walkscore(person_id: str, walkscore: WalkScoreBase):
    db_walkscore = WalkScore(person_id=person_id,
                             mon=walkscore.mon,
                             tue=walkscore.tue,
                             wed=walkscore.wed,
                             thu=walkscore.thu,
                             fri=walkscore.fri,
                             sat=walkscore.sat,
                             sun=walkscore.sun)

    ws = session.query(WalkScore).filter(
        WalkScore.person_id == person_id).first()
    if(ws is not None):
        session.delete(ws)
        session.commit()
    session.add(db_walkscore)
    session.commit()
    session.refresh(db_walkscore)
    return db_walkscore


@ app.post("/login")
async def login(logindata: LoginData):
    user = session.query(User).filter(
        User.person_id == logindata.person_id).filter(
        User.password == logindata.password).first()
    if(user is None):
        result = False
        message = "Fail"
    else:
        result = True
        message = "Success"
    return {"result": result, "Message": message, "user": user}


@ app.get("/users")
async def get_all_user():
    users = session.query(User).limit(50).all()
    return users

@ app.get("/users/{person_id}")
async def get_user_by_id(person_id: str):
    user = session.query(User).filter(User.person_id == person_id).first()
    return user

@app.post("/check/id")
async def isExistUser(UserId: UserId):
    isExist = False
    user = session.query(User).filter(User.person_id == UserId.person_id).first()
    if (user):
        isExist = True
    return {"isExist": isExist}

@ app.post("/join")
async def save_user(user: UserCreate):
    # 추후 암호화
    fake_password = user.password
    db_user = User(person_id=user.person_id,
                   nickname=user.nickname,
                   age=user.age,
                   password=fake_password,
                   gender=user.gender)
    session.add(db_user)
    session.commit()
    session.refresh(db_user)
    return {"id": db_user.person_id} 

@ app.get("/rawsteps/{person_id}/week")
def read_steps(person_id: str):
    result = session.query(WalkCount).filter(
        WalkCount.person_id == person_id).filter(
        WalkCount.walk_date >= datetime.today() - timedelta(days=8)
    ).filter(
        WalkCount.walk_date < datetime.today()
    ).all()
    return result

@ app.get("/steps/{person_id}/week")
def read_steps(person_id: str):
    steps_dict_list = getStepsLastNDays(person_id, 21)
    return getProphetResult(person_id, steps_dict_list)
    # return getStepsLastNDays(person_id, 21)

@ app.post("/steps/{person_id}/day")
async def save_walkcount_day(person_id: str, steps: Steps):
    # steps_list
    # [0] YYYY-mm-dd [1] steps(n)
    steps_list = steps.dict()["targetSteps"]
    if steps_list:
        save_walkcount(person_id, steps_list[0], steps_list[1])

@ app.post("/initialData")
async def save_walkcount_day(steps: Steps):
    # steps_list
    # [0] YYYY-mm-dd [1] steps(n)
    steps_list = steps.dict()["targetSteps"]
    person_id = steps.person_id
    if steps_list:
        save_walkcount(person_id, steps_list[0], steps_list[1])

@ app.post("/steps/{person_id}/days")
async def save_walkcount_days(person_id: str, steps: Steps):
    steps_list = steps.dict()["targetSteps"]
    for i in range(0, len(steps_list), 2):
        _date = steps_list[i]
        _step = steps_list[i+1]
    if steps_list:
        save_walkcount(person_id, steps_list[0], steps_list[1])
    return {"count":len(steps_list)/2, "first_date": steps_list[0], "last_date": steps_list[len(steps_list)-2]}

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
