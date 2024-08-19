class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class BusService:
    def optimize_boarding_process(self, head, k):
        # If the list is empty or k is 1, no change is needed
        if head is None or k == 1:
            return head

        # Dummy node to handle edge cases
        dummy = ListNode(0)
        dummy.next = head
        prev = dummy
        curr = head

        while curr:
            # Check if there are at least k nodes left to reverse
            kth = self.get_kth_node(curr, k)
            if not kth:
                break

            next_group_start = kth.next

            # Reverse the group from curr to kth
            reversed_head = self.reverse_group(curr, kth.next)

            # Connect the previous part with the reversed group
            prev.next = reversed_head
            curr.next = next_group_start

            # Move prev and curr pointers
            prev = curr
            curr = next_group_start

        return dummy.next

    def get_kth_node(self, node, k):
        while node and k > 1:
            node = node.next
            k -= 1
        return node

    def reverse_group(self, start, end):
        prev = None
        curr = start
        while curr != end:
            next_temp = curr.next
            curr.next = prev
            prev = curr
            curr = next_temp
        return prev

    # Helper method to create a linked list from an array
    @staticmethod
    def create_list(arr):
        if not arr:
            return None
        dummy = ListNode(0)
        current = dummy
        for val in arr:
            current.next = ListNode(val)
            current = current.next
        return dummy.next

    # Helper method to convert a linked list to an array
    @staticmethod
    def list_to_array(head):
        result = []
        while head:
            result.append(head.val)
            head = head.next
        return result


if __name__ == "__main__":
    solution = BusService()

    # Example 1
    arr1 = [1, 2, 3, 4, 5]
    head1 = solution.create_list(arr1)
    result1 = solution.optimize_boarding_process(head1, 2)
    print("Example 1 Output:", solution.list_to_array(result1))

    # Example 2
    arr2 = [1, 2, 3, 4, 5]
    head2 = solution.create_list(arr2)
    result2 = solution.optimize_boarding_process(head2, 3)
    print("Example 2 Output:", solution.list_to_array(result2))
