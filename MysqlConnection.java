import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

final  class MysqlConnectTread implements Runnable {
    AtomicLong conCount;

    public  MysqlConnectTread(AtomicLong conCount) {
        this.conCount = conCount;
    }

    public void run() {
            Connection conn = null;
            String url = "jdbc:mysql://drdsfacbrv75697npublic.drds.aliyuncs.com:3306/flink_subscribe";
            try {
                Class.forName("com.mysql.jdbc.Driver");
                while(true) {
                    long beginTime = System.currentTimeMillis();
                    conn = DriverManager.getConnection(url, "baoyuansong", "q1w2E#R$");
                    Statement statement = conn.createStatement();
                    statement.execute("SET NAMES 'utf8mb4'");
                    statement.close();
                    conn.close();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("connection time:" + String.valueOf(System.currentTimeMillis() - beginTime));
                    this.conCount.getAndIncrement();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}

public class MysqlConnection {
    public static void main(String[] args) {
        AtomicLong callCount = new AtomicLong(0);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            fixedThreadPool.execute(new MysqlConnectTread(callCount));
        }

        while(true){
            try {
                long currentTimeMillis = System.currentTimeMillis();
                System.out.println("connection/second:" + String.valueOf((double)(callCount.get()*1000) / (double)((currentTimeMillis - beginTime))));
                Thread.sleep(1000000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
