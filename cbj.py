import csv
from datetime import datetime, timedelta

dict = {}
week = {0:'월요일', 1:'화요일', 2:'수요일', 3:'목요일', 4:'금요일', 5:'토요일', 6:'일요일'}

f = open('simple_activity.csv', 'r', encoding='utf-8')
rdr = csv.reader(f)
next(rdr) # 첫 줄 건너뛰기

for line in rdr:
    datetime_date = datetime.strptime(line[6][:10], "%Y-%m-%d").weekday() # 요일 숫자
    weekIndex = int(datetime.strptime(line[6][:10], "%Y-%m-%d").date().strftime("%V")) # 입력 날짜의 주 수
    walkNum = int(line[1])
    personId = line[0] # 사람 ID
    year = int(line[6][:4]) # 입력 날짜의 연도
    month = line[6][5:7] # 입력 날짜의 달
    date = line[6][8:10] # 입력 날짜의 일
    if personId in dict: # 사람 ID가 dict에 존재한다면
        if year in dict[personId]: # 입력되는 사람ID의 날짜 연도가 존재한다면
            if datetime_date in dict[personId][year][weekIndex]: # 만약 해당 주의 요일이 여러개면
                dict[personId][year][weekIndex][datetime_date] += walkNum # 해당 요일에 걸음수 누적
            else: # 연도의 주 수만큼 다 돌았는데도 해당 주에 요일이 없으면
                dict[personId][year][weekIndex][datetime_date] = walkNum # 해당 주에 요일, 걸음수 추가
                # dict[personId][year][weekIndex].append((datetime_date, walkNum)) # 해당 주에 요일, 걸음수 추가

        else: # 입력되는 사람ID의 날짜 연도가 존재하지 않으면
            lastday = str(year) + "-12-31" # 해당 연도의 마지말 날 구하고
            lastIndex = int(datetime.strptime(lastday, "%Y-%m-%d").date().strftime("%V")) # 연도의 마지막 날의 주 수 구하기
            if lastIndex == 1 and month == '12': # 만약에 입력 연도의 마지막 주 수가 1이고 입력 월이 12월 이면 -> 그 다음 연도의 첫 주(1/4)랑 같은 주 라는 뜻 -> 그 뜻은 그 다음 연도의 첫 주에 넣어야 한다는 뜻 -> 그 뜻은 다음 연도의 마지막 주를 알아야 한다는 뜻
            # ex) 2018-12-27 이 입력값이면, 2019년도의 1주차에 넣음 (2019년도의 마지막 주 알기)
                year += 1  # 2019
                lastday = datetime.strptime(str(year + 1) + "-01-04", "%Y-%m-%d").date() + timedelta(weeks=-1)  # 2020-01-04 - 1주 = 2019-12-xx (마지막주)
                lastIndex = int(lastday.strftime("%V"))
            else:
            # ex) 2018-06-03 이 입력값이면, 2018년도의 해당 주에 넣음 (2018년도의 마지막 주 알기)
                lastday = datetime.strptime(str(year + 1) + "-01-04", "%Y-%m-%d").date() + timedelta(weeks=-1)  # 2019-01-04 - 1주 = 2018-12-xx (마지막주)
                lastIndex = int(lastday.strftime("%V"))

            dict[personId][year] = [{} for i in range(lastIndex + 1)] # 마지막 주만큼 빈 배열 만들기 -> 주는 1부터 시작해서 +1 해줌
            # dict[personId][year][weekIndex].append((line[1], line[6][:10], datetime_date)) # 해당 주에 걸음수, 날짜, 요일 추가
            if datetime_date in dict[personId][year][weekIndex]: # 만약 해당 주의 요일이 여러개면
                dict[personId][year][weekIndex][datetime_date] += walkNum # 해당 요일에 걸음수 누적
            else: # 연도의 주 수만큼 다 돌았는데도 해당 주에 요일이 없으면
                dict[personId][year][weekIndex][datetime_date] = walkNum # 해당 주에 요일, 걸음수 추가

    else: # 사람 ID가 dict에 존재하지 않으면
        dict[personId] = {} # 사람 ID dictionary 추가
        lastday = str(year)+"-12-31"
        lastIndex = int(datetime.strptime(lastday, "%Y-%m-%d").date().strftime("%V"))
        if lastIndex == 1 and month == '12':
            year += 1 # 2019
            lastday = datetime.strptime(str(year + 1) + "-01-04", "%Y-%m-%d").date() + timedelta(weeks=-1) # 2020-01-04 - 1주 = 2019-12-xx (마지막주)
            lastIndex = int(lastday.strftime("%V"))
        else:
            lastday = datetime.strptime(str(year + 1) + "-01-04", "%Y-%m-%d").date() + timedelta(weeks=-1) # 2019-01-04 - 1주 = 2018-12-xx (마지막주)
            lastIndex = int(lastday.strftime("%V"))

        dict[personId][year] = [{} for i in range(lastIndex + 1)]
        # dict[personId][year][weekIndex].append((line[1], line[6][:10], datetime_date))
        if datetime_date in dict[personId][year][weekIndex]:  # 만약 해당 주의 요일이 여러개면
            dict[personId][year][weekIndex][datetime_date] += walkNum  # 해당 요일에 걸음수 누적
        else:  # 연도의 주 수만큼 다 돌았는데도 해당 주에 요일이 없으면
            dict[personId][year][weekIndex][datetime_date] = walkNum  # 해당 주에 요일, 걸음수 추가

# print(dict)
for key, value in dict.items():
    # print(key, value)
    print(key + " : ", end='') # personId
    for key2, value2 in value.items():
        print(str(key2) + " : ", end='') # year
        for i in range(1, len(value2)): # 연도의 주 수만큼 루프
            if len(value2[i]) == 0: # 안 걸은 주 수 건너뛰기 (생략 가능)
                continue
            sorted_dict = sorted(value2[i].items(), key= lambda item: item[1], reverse = True) # 걸음 수 별 내림차순 정렬
            print(str(i) + "주차 : ", sorted_dict)
f.close()