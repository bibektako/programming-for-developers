class MountainPath:
    @staticmethod
    def longest_trek(elevations, max_diff):
        max_distance = 0
        index = 1
        while index < len(elevations):
            if elevations[index] > elevations[index - 1]:
                trek_length = 1
                while index + trek_length < len(elevations) and elevations[index + trek_length] - elevations[index + trek_length - 1] <= max_diff:
                    trek_length += 1
                max_distance = max(max_distance, trek_length)
            index += 1
        return max_distance


if __name__ == "__main__":
    elevations = [4, 2, 1, 4, 3, 4, 5, 8, 15]
    max_diff = 3
    print(MountainPath.longest_trek(elevations, max_diff))
