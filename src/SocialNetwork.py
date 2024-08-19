from collections import deque

class SocialNetwork:
    def __init__(self):
        self.parent = []
        self.restrictions = []

    def process_requests(self, num_people, restrictions_list, requests_list):
        self.parent = list(range(num_people))
        self.restrictions = [set() for _ in range(num_people)]

        for restriction in restrictions_list:
            self.restrictions[restriction[0]].add(restriction[1])
            self.restrictions[restriction[1]].add(restriction[0])

        responses = []
        for request in requests_list:
            if self.are_friends_possible(request[0], request[1]):
                self.merge_sets(request[0], request[1])
                responses.append("approved")
            else:
                responses.append("denied")

        return responses

    def are_friends_possible(self, person_a, person_b):
        root_a = self.find_root(person_a)
        root_b = self.find_root(person_b)

        if root_a == root_b:
            return True

        queue = deque([root_a])
        visited = {root_a}

        while queue:
            current = queue.popleft()
            if root_b in self.restrictions[current]:
                return False

            for neighbor in range(len(self.parent)):
                if self.find_root(neighbor) == current and neighbor not in visited:
                    queue.append(neighbor)
                    visited.add(neighbor)

        return True

    def find_root(self, person):
        if self.parent[person] != person:
            self.parent[person] = self.find_root(self.parent[person])
        return self.parent[person]

    def merge_sets(self, person_a, person_b):
        root_a = self.find_root(person_a)
        root_b = self.find_root(person_b)
        if root_a != root_b:
            self.parent[root_b] = root_a

if __name__ == "__main__":
    network = SocialNetwork()

    # Example 1
    num_people1 = 3
    restrictions_list1 = [[0, 1]]
    requests_list1 = [[0, 2], [2, 1]]
    print(network.process_requests(num_people1, restrictions_list1, requests_list1))

    # Example 2
    num_people2 = 5
    restrictions_list2 = [[0, 1], [1, 2], [2, 3]]
    requests_list2 = [[0, 4], [1, 2], [3, 1], [3, 4]]
    print(network.process_requests(num_people2, restrictions_list2, requests_list2))
