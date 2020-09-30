import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> edgeList = new ArrayList<>();

        ArrayList<Integer> list1 = new ArrayList<>();
        list1.add(1);
        list1.add(2);
        list1.add(3);

        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(4);
        list2.add(5);

        edgeList.add(list1);
        edgeList.add(list2);

        System.out.println("===1===");
        System.out.println(edgeList.toString());

        //这种方式无法删除成功
        ArrayList<Integer> list3 = new ArrayList<>();
        list3.add(5);
        list3.add(4);
        edgeList.remove(list3);

        //这种方式能删除成功
        ArrayList<Integer> list4 = new ArrayList<>();
        list3.add(4);
        list3.add(5);
        edgeList.remove(list4);

        //要想删除list中的list,就必须将每个edge中的node节点排序

        System.out.println("===2===");
        System.out.println(edgeList.toString());

    }

}
