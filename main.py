from flask import Flask, request, make_response, jsonify
from flask_restful import Api, Resource
from process import getProphetResult
app = Flask(__name__)
api = Api(app)

# dummy data
weeksteps = {
    '2022-09-01': 1000,
    '2022-09-02': 2000,
    '2022-09-03': 3000,
    '2022-09-04': 4000,
    '2022-09-05': 5000,
    '2022-09-06': 6000,
    '2022-09-07': 7000,
}


class StepManager(Resource):
    def get(self, walk_date):
        return make_response(jsonify(weeksteps[walk_date]), 200)

    def post(self):
        global weeksteps
        data = request.json
        date_steps_list = data['data']
        process_result = getProphetResult(date_steps_list)
        # for idx in range(len(date_steps_list)):
        #     walk_date = date_steps_list[idx]['date']
        #     walk_steps = date_steps_list[idx]['steps']
        #     weeksteps[walk_date] = walk_steps
        return make_response(jsonify(process_result), 200)


class Test(Resource):
    def post(self):
        data = request.json
        return make_response(data, 200)


api.add_resource(Test, '/test')
api.add_resource(StepManager, '/steps', '/steps/<string:walk_date>')


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=43210)
