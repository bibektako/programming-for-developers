import numpy as np
import random

class TspPathOptimizer:
    def __init__(self, city_count):
        self.city_count = city_count
        self.city_distances = np.zeros((city_count, city_count))
        self.route_fitness = 0.0

    def define_distances(self, city_distances):
        self.city_distances = city_distances

    def optimize(self):
        # Initialize a random tour
        route = list(range(self.city_count))
        random.shuffle(route)

        # Compute the initial fitness
        self.route_fitness = self.compute_fitness(route)

        # Hill climbing process
        while True:
            neighboring_route = self.get_neighboring_route(route)
            neighboring_fitness = self.compute_fitness(neighboring_route)

            if neighboring_fitness < self.route_fitness:  # Better route found
                route = neighboring_route
                self.route_fitness = neighboring_fitness
            else:
                break  # No improvement, stop climbing

        print("Optimal route found:", route)
        print("Minimal travel distance:", self.route_fitness)

    def get_neighboring_route(self, route):
        # Swap two randomly chosen cities to create a neighboring solution
        i, j = random.sample(range(self.city_count), 2)

        new_route = route[:]
        new_route[i], new_route[j] = new_route[j], new_route[i]

        return new_route

    def compute_fitness(self, route):
        distance_sum = 0.0

        for i in range(self.city_count - 1):
            distance_sum += self.city_distances[route[i]][route[i + 1]]

        # Include the return distance from the last city to the first city
        distance_sum += self.city_distances[route[-1]][route[0]]

        return distance_sum

# Example usage
if __name__ == "__main__":
    # Sample distances between cities for a TSP problem
    city_distances = np.array([
        [0, 10, 15, 20],
        [10, 0, 25, 18],
        [15, 25, 0, 22],
        [20, 18, 22, 0]
    ])

    tsp_solver = TspPathOptimizer(4)
    tsp_solver.define_distances(city_distances)
    tsp_solver.optimize()
