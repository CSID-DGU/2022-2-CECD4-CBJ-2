import csv
from enum import Enum
import pandas as pd
import matplotlib.pyplot as plt

from pytz import timezone
from prophet import Prophet
from datetime import datetime, timedelta, date

dayAndStepsDict = {}
week = {0: '월요일', 1: '화요일', 2: '수요일', 3: '목요일', 4: '금요일', 5: '토요일', 6: '일요일'}


# filename = 'date-steps-220624-220924.csv'
# filename_ = 'simple_activity_person5.csv'
# f = open(filename, 'r', encoding='utf-8')
# rdr = csv.reader(f)
# next(rdr)  # 첫 줄 건너뛰기

period = 7  # forecast future 28 days (4 weeks)
min_steps = 1000
max_steps = 15000

step_lowerbound = 2000
step_upperbound = 10000

# 너무 적거나 너무 큰 데이터 자르기


def removeOutOf(steps, min, max):
    if (steps > max):
        steps = max
    if (steps < min):
        steps = min
    return steps


class Intensity(Enum):
    HIGH = 3
    MID = 2
    LOW = 1


intensityWeight = {Intensity.HIGH: 1.2, Intensity.MID: 1.0, Intensity.LOW: 0.8}

scoreWeight = {"5": 1.5, "4": 1.2, "3": 1.0, "2": 0.8, "1": 0.5}

# 예측점수
# (input) score : 1~5 (output) steps * weight


def applyScore(steps, score):
    weight = scoreWeight[str(score)]
    return steps * weight

# 상중하 운동강도
# (input) Intensity.XX (output) steps * weight


def applyIntensity(steps, intensity):
    weight = intensityWeight[intensity]
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
dummy_weight = Intensity.HIGH
# week_score[day] = weight
dummy_score = [3, 3, 3, 3, 3, 3, 3]

today = datetime.now(timezone('Asia/Seoul'))


def getDateStrFrom(line):
    # return line[0].replace(".", "-")s
    return line['date']


def getStepsFrom(line):
    return int(line['steps'])


# In[160]:

def getProphetResult(date_step_list):
    for line in date_step_list:
        steps = getStepsFrom(line)
        # steps = 5000
        dateStr = getDateStrFrom(line)

        # 1000~15000 밖의 데이터 제거
        steps = removeOutOf(steps, min_steps, max_steps)

        # 500 단위로 반올림
        steps = roundBy(steps, 500)

        # 2000~10000 사이로 정규화
        steps = normalize(steps, 2000, 10000)

        # 운동 강도 상,중,하 반영
        steps = applyIntensity(steps, Intensity.HIGH)
        # 특정 일의 걸음 예측 점수 반영하기 (index 0: 월요일)
        steps = applyScore(steps, dummy_score[0])

        dateobj = datetime.strptime(dateStr, '%Y-%m-%d')

        # 220924 기준임
        today_date = datetime.strptime('2022-09-24', '%Y-%m-%d')
        # 3주 전
        standard_date = today_date - timedelta(weeks=3)
        # 학습 범위는 과거 3주
        if dateobj < standard_date:
            continue
        print(dateStr + ": " + str(steps))
        dayAndStepsDict[dateStr] = steps

    dayAndStepsList = sorted(dayAndStepsDict.items(
    ), key=lambda x: datetime.strptime(x[0], '%Y-%m-%d'))

    df = pd.DataFrame(dayAndStepsList, columns=['ds', 'y'])

    # 상한 하한
    # print(df.tail(14))
    # f.close()

    m = Prophet(interval_width=0.6)

    m.fit(df)
    future = m.make_future_dataframe(periods=period)

    forecast = m.predict(future)

    ds_list = [val for sublist in forecast[['ds']].values for val in sublist]
    yhat_list = list(
        map(round, [val for sublist in forecast[['yhat']].values for val in sublist]))
    yhat_lower_list = list(map(
        round, [val for sublist in forecast[['yhat_lower']].values for val in sublist]))
    yhat_upper_list = list(map(
        round, [val for sublist in forecast[['yhat_upper']].values for val in sublist]))

    return yhat_list
