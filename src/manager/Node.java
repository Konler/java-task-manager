package manager;

import task.Task;

public class Node <E> {

        public Task data;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, Task data, Node<E> next) {
            this.data = data;
        }
        public Node getNext() {
                return next;
        }

        public Node getPrev() {
                return prev;
        }

        public Task getTask() {
                return data;
        }
        public void setNext(Node next) {
                this.next = next;
        }

        public void setPrev(Node prev) {
                this.prev = prev;
        }


}
