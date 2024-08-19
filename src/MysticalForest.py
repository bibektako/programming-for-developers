class Node:
    def __init__(self, value):
        self.value = value
        self.left_child = None
        self.right_child = None

class Forest:
    def find_max_mystical_grove(self, node, go_left):
        if node is None:
            return 0

        max_grove_value = node.value
        if go_left:
            max_grove_value = max(max_grove_value, self.find_max_mystical_grove(node.right_child, False))
        else:
            max_grove_value = max(max_grove_value, self.find_max_mystical_grove(node.left_child, True))

        return max_grove_value

    def calculate_max_value(self, node):
        if node is None:
            return 0

        left_max = self.find_max_mystical_grove(node.left_child, True)
        right_max = self.find_max_mystical_grove(node.right_child, False)

        total_grove_value = left_max + right_max + node.value
        return total_grove_value

    def find_largest_mystical_grove(self, node):
        if node is None:
            return 0

        largest_grove_value = self.calculate_max_value(node)
        left_largest = self.find_largest_mystical_grove(node.left_child)
        right_largest = self.find_largest_mystical_grove(node.right_child)

        return max(largest_grove_value, left_largest, right_largest)

if __name__ == "__main__":
    mystical_forest = Forest()

    # Constructing the mystical forest
    root = Node(1)
    root.left_child = Node(4)
    root.right_child = Node(3)
    root.left_child.left_child = Node(2)
    root.left_child.right_child = Node(4)
    root.right_child.left_child = Node(2)
    root.right_child.right_child = Node(5)

    max_forest_value = mystical_forest.find_largest_mystical_grove(root)
    print("The largest mystical grove value is:", max_forest_value)
