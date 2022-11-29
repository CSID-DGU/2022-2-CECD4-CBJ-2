import csv
from enum import Enum
import pandas as pd

from pytz import timezone
from prophet import Prophet
from datetime import datetime, timedelta, date

import os
import json

dayAndStepsDict = {}
week = {0: '월요일', 1: '화요일', 2: '수요일', 3: '목요일', 4: '금요일', 5: '토요일', 6: '일요일'}

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
CONFIG_FILE = os.path.join(BASE_DIR, "prophet.json")
_configs = json.loads(open(CONFIG_FILE, "r", encoding="utf-8-sig").read())

PROPHET_CONFIG = _configs["prophet"]
period = PROPHET_CONFIG['period']  # forecast future 7 days

# 너무 적거나 너무 큰 데이터 자르기
interval_width = PROPHET_CONFIG['interval_width']
min_steps = PROPHET_CONFIG['min_steps']
max_steps = PROPHET_CONFIG['max_steps']
round_by = PROPHET_CONFIG['round_by']
normalize_min = PROPHET_CONFIG['normalize_min']
normalize_max = PROPHET_CONFIG['normalize_max']


def removeOutOf(steps, min, max):
    if (steps > max):
        steps = max
    if (steps < min):
        steps = min
    return steps




strengthWeight = {"상": 1.2, "중": 1.0, "하": 0.8}

scoreWeight = {"5": 1.5, "4": 1.25, "3": 1.0, "2": 0.75, "1": 0.5}

# 예측점수
# (input) score : 1~5 (output) steps * weight


def applyScore(steps, score):
    weight = scoreWeight[str(score)]
    return steps * weight

# 상중하 운동강도
# (input) Intensity.XX (output) steps * weight

def applyStrength(steps, strength):
    weight = strengthWeight[strength]
    return steps * weight


# roundBy(2550, 500) -> 2500
# roundBy(2750, 500) -> 3000
def roundBy(target, by):
    r = target % by  # 나머지
    if r >= (by / 2):  # 올림
        target += by - r
    else:
        target -= r  # 내림
    return target

# lower~upper 사이 값으로 줄이기


def normalize(value, lower, upper):
    return (value / max_steps) * (upper-lower) + lower


# dummuy data
# week_score[day] = weight
# dummy_score = [3, 3, 3, 3, 3, 3, 3]


def getDateStrFrom(line):
    # return line[0].replace(".", "-")s
    return line[0]


def getStepsFrom(line):
    return int(line[1])


def applyProphet(date_step_list, score_and_strength, isApplyScore):
    for line in date_step_list:
        steps = getStepsFrom(line)
        dateStr = getDateStrFrom(line)

        # 1000~15000 밖의 데이터 제거
        steps = removeOutOf(steps, min_steps, max_steps)

        # 500 단위로 반올림 X
        # steps = roundBy(steps, round_by)

        # 2000~10000 사이로 정규화
        steps = normalize(steps, normalize_min, normalize_max)

        # 운동 강도 상,중,하 반영
        steps = applyStrength(steps, score_and_strength["strength"])

        dayAndStepsDict[dateStr] = steps

    dayAndStepsList = sorted(dayAndStepsDict.items(
    ), key=lambda x: datetime.strptime(x[0], '%Y-%m-%d'))

    df = pd.DataFrame(dayAndStepsList, columns=['ds', 'y'])

    m = Prophet(interval_width=interval_width)

    m.fit(df)
    future = m.make_future_dataframe(periods=period)

    forecast = m.predict(future)

    # 여기부터 다시
    res = {
        "mid": [],
        "low": [],
        "high": []
    }
    # print([val for sublist in forecast.values for val in sublist])

    ds_list = [val for sublist in forecast[[
        'ds']].values for val in sublist][(-1*period):]
    yhat_list = list(
        map(round, [val for sublist in forecast[['yhat']].values for val in sublist]))[(-1*period):]
    yhat_lower_list = list(map(
        round, [val for sublist in forecast[['yhat_lower']].values for val in sublist]))[(-1*period):]
    yhat_upper_list = list(map(
        round, [val for sublist in forecast[['yhat_upper']].values for val in sublist]))[(-1*period):]

    for idx, key in enumerate(ds_list):
        key = str(key)[:10]
        res["mid"].append({
            "date": key,
            "steps": yhat_list[idx],
            "dayOfWeek": datetime.strptime(
                key, '%Y-%m-%d').strftime('%a').lower()
        })
        res["low"].append({"date": key,
                           "steps": yhat_lower_list[idx],
                           "dayOfWeek": datetime.strptime(
                               key, '%Y-%m-%d').strftime('%a').lower()})

        res["high"].append({"date": key,
                            "steps": yhat_upper_list[idx],
                            "dayOfWeek": datetime.strptime(
                                key, '%Y-%m-%d').strftime('%a').lower()})

    if(isApplyScore): # for test
      for obj in res["mid"]:
          obj["steps"] = round(applyScore(
              obj["steps"], score_and_strength["score"].get_score_by_day(obj["dayOfWeek"])))

      for obj in res["high"]:
          obj["steps"] = round(applyScore(
              obj["steps"], score_and_strength["score"].get_score_by_day(obj["dayOfWeek"])))

      for obj in res["low"]:
          obj["steps"] = round(applyScore(
              obj["steps"], score_and_strength["score"].get_score_by_day(obj["dayOfWeek"])))
    return res
