package view;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Display extends JFrame {

    // Constructor để tạo biểu đồ
    public Display(String title, Map<String, Integer> dataMap) {
        super(title);

        // Tạo dataset từ dữ liệu
        DefaultCategoryDataset dataset = createDataset(dataMap);

        // Tạo biểu đồ cột
        JFreeChart barChart = ChartFactory.createBarChart(
                "Thống kê số điểm theo ID", // Tiêu đề biểu đồ
                "ID", // Tiêu đề trục x
                "Số điểm", // Tiêu đề trục y
                dataset // Dataset để vẽ biểu đồ
        );

        // Tạo panel chứa biểu đồ
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }

    // Phương thức để tạo dataset từ dữ liệu
    private DefaultCategoryDataset createDataset(Map<String, Integer> dataMap) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Thêm dữ liệu vào dataset
        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            dataset.addValue(entry.getValue(), "Số điểm", entry.getKey());
        }

        return dataset;
    }

    // Phương thức để đọc dữ liệu từ file
    private static Map<String, Integer> readData(String filePath) {
        Map<String, Integer> dataMap = new HashMap<>();
        String line = null;

        try (BufferedReader br = new BufferedReader(new FileReader("C:\\KhaiPhaDuLieu\\data_output.csv"))) {
            // Bỏ qua dòng tiêu đề
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");  // Giả sử dữ liệu cách nhau bằng dấu phẩy
                if (parts.length < 2) {
                    System.err.println("Invalid line format: " + line);
                    continue;  // Bỏ qua dòng không hợp lệ
                }
                String id = parts[parts.length-1].trim();  // ID
                dataMap.put(id, dataMap.getOrDefault(id, 0) + 1);  // Tăng số điểm cho ID
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error in line format: " + line);
        }

        return dataMap;
    }

    // Phương thức main để chạy ứng dụng
    public static void main(String[] args) {
        // Đường dẫn đến file data_output.txt
        String filePath = "D:\\phan_tich_du_lieu_lon\\BTL\\data_output.csv"; // Thay đổi đường dẫn nếu cần

        // Đọc dữ liệu từ file
        Map<String, Integer> dataMap = readData(filePath);

        // Tạo biểu đồ
        Display chart = new Display("Biểu đồ số điểm theo ID", dataMap);
        chart.setSize(800, 600);  // Kích thước cửa sổ hiển thị
        chart.setLocationRelativeTo(null);  // Căn giữa màn hình
        chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Thoát chương trình khi đóng cửa sổ
        chart.setVisible(true);  // Hiển thị biểu đồ
    }
}