public class Stack<T> {
    //Node class for stack elements
    private static class Node<E> {
        E data;
        Node<E> next;

        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<T> top;

    //Stack constructor
    public Stack() {
        top = null;
    }

    //Check if the stack is empty
    public boolean isEmpty() {
        return top == null;
    }

    //Push an item onto the stack
    public void push(T item) {
        top = new Node<>(item, top);
    }

    //Pop the top item from the stack
    public T pop() {
        if (isEmpty()) {
            return null; //or throw exception
        }
        T item = top.data;
        top = top.next;
        return item;
    }

    //Peek at the top item without removing it
    public T peek() {
        if (isEmpty()) {
            return null; //or throw exception
        }
        return top.data;
    }
}
