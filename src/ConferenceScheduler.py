class Conference:
    def __init__(self, start_time, end_time, index):
        self.start_time = start_time
        self.end_time = end_time
        self.index = index

class ScheduleComparator:
    def __call__(self, session1, session2):
        if session1.end_time < session2.end_time:
            return -1
        elif session1.end_time > session2.end_time:
            return 1
        elif session1.index < session2.index:
            return -1
        return 1

def schedule_conferences(start_times, end_times, count):
    sessions = []

    for i in range(count):
        sessions.append(Conference(start_times[i], end_times[i], i + 1))

    sessions.sort(key=lambda session: (session.end_time, session.index))

    scheduled_sessions = [sessions[0].index]
    time_limit = sessions[0].end_time

    for i in range(1, count):
        if sessions[i].start_time > time_limit:
            time_limit = sessions[i].end_time
            scheduled_sessions.append(sessions[i].index)

    print("The order in which the conferences will be scheduled is:")
    print(" ".join(map(str, scheduled_sessions)))

if __name__ == "__main__":
    count = 6
    start_times = [1, 3, 0, 5, 8, 5]
    end_times = [2, 4, 5, 7, 9, 9]
    schedule_conferences(start_times, end_times, count)
