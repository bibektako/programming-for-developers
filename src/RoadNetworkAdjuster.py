import sys

def adjust_travel_times(road_map, start, end, max_travel_time):
    n = len(road_map)
    # Array to store shortest times from the start point to each destination
    travel_times = [sys.maxsize] * n
    travel_times[start] = 0

    # Relaxing the edges repeatedly
    for _ in range(n - 1):
        for road in road_map:
            u, v, time = road
            if time == -1:
                time = 1  # Temporary weight for roads still under construction
            if travel_times[u] != sys.maxsize and travel_times[u] + time < travel_times[v]:
                travel_times[v] = travel_times[u] + time

    # Detect negative-weight cycles
    has_negative_cycle = False
    for road in road_map:
        u, v, time = road
        if time == -1:
            time = 1
        if travel_times[u] != sys.maxsize and travel_times[u] + time < travel_times[v]:
            has_negative_cycle = True
            break

    if has_negative_cycle:
        print("Error: Detected a negative-weight cycle in the road network.")
        return

    # Modify roads still under construction
    modified = True
    while modified:
        modified = False
        for road in road_map:
            u, v, time = road
            if time == -1 and travel_times[u] != sys.maxsize:
                updated_time = min(2000000000, max_travel_time // (travel_times[u] + 1) + 1)
                if travel_times[u] + updated_time < travel_times[v]:
                    road[2] = updated_time
                    travel_times[v] = travel_times[u] + updated_time
                    modified = True

    if travel_times[end] <= max_travel_time:
        print("Modified road network:", road_map)
    else:
        print("It's not possible to reach the destination within the given time limit.")

if __name__ == "__main__":
    # Example road network with -1 indicating unconstructed roads
    road_map = [[0, 1, -1], [1, 2, -1], [2, 3, -1], [3, 0, -1]]
    start_point = 0
    end_point = 1
    time_limit = 5

    adjust_travel_times(road_map, start_point, end_point, time_limit)
