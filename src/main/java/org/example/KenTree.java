package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KenTree {
    private class KenNode {
        private final int key;
        private final List<String> value;

        private KenNode left;
        private KenNode right;

        public KenNode(String str) {
            this.key = str.length();
            this.value = new ArrayList<>(List.of(str));
        }

        private KenNode getLast() {
            KenNode last = this;
            while (last.right != null) {
                last = last.right;
            }
            return last;
        }

        private KenNode getFirst() {
            KenNode first = this;
            while (first.left != null) {
                first = first.left;
            }
            return first;
        }
    }

    private final KenNode root;

    public KenTree(String first) {
        this.root = new KenNode(first);
    }

    public List<List<String>> sortedOrder() {
        List<List<String>> result = new ArrayList<>();
        inOrderTraversal(this.root, result);
        return result;
    }

    public void addVal(String str) {
        KenNode prev = null;
        KenNode curr = this.root;
        KenNode toAdd = new KenNode(str);
        while (curr != null) {
            prev = curr;
            if (toAdd.key < curr.key) {
                curr = curr.left;
            } else if (toAdd.key > curr.key) {
                curr = curr.right;
            } else {
                curr.value.add(str);
                return;
            }
        }
        // root can never be null so this will hold bcoz we don't currently support deleting
        if  (prev != null) {
            prev.right = toAdd;
        }
    }

    private void inOrderTraversal(KenNode node, List<List<String>> result) {
        if (node != null) {
            inOrderTraversal(node.left, result);
            result.add(node.value);
            inOrderTraversal(node.right, result);
        }
    }

}
