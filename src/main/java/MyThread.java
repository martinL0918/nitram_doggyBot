public class MyThread extends Thread {
    public void run(){
        for (int i=0;i<5;i++){
            try {
                System.out.println("Time counter: "+i);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
