package cinema.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class Controller {

    static final Integer totalRows = 9;
    static final Integer totalColumns = 9;
    private static Map<String, Object> map = new HashMap<>();
    private static String[][] purchasedSeats = new String[totalRows][totalColumns];

    public Controller() {
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                purchasedSeats[i][j] = "available";
            }
        }
        map.put("total_rows", totalRows);
        map.put("total_columns", totalColumns);

        List<SeatNo> available_seats = new ArrayList<>();
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                available_seats.add(new SeatNo(i + 1,j + 1, (i < 4 ? 10 : 8)));
            }
        }
        map.put("available_seats", available_seats);
        map.get("available_seats");
    }

    @PostMapping("/stats")
    public Object showStats(@RequestParam(value = "password", required = false) String password) {
        if (password == null || !"super_secret".equals(password) )
            return new ResponseEntity(Map.of("error", "The password is wrong!"), HttpStatus.UNAUTHORIZED);
        Income income = new Income(purchasedSeats.length * purchasedSeats[0].length);
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if (!"available".equals(purchasedSeats[i][j])) {
                    income.setCurrent_income(income.getCurrent_income() + (i < 4 ? 10 : 8));
                    income.setNumber_of_available_seats(income.getNumber_of_available_seats() - 1);
                    income.setNumber_of_purchased_tickets(income.getNumber_of_purchased_tickets() + 1);
                }
            }
        }
        return income;
    }

    @PostMapping("/return")
    public Object returnTicket(@RequestBody String str) {
        int ii = -1, jj = -1;
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if (!"available".equals(purchasedSeats[i][j]) && str.contains(purchasedSeats[i][j])) {
                    ii = i;
                    jj = j;
                }
            }
        }
        if (ii == -1)
            return new ResponseEntity(Map.of("error", "Wrong token!"), HttpStatus.BAD_REQUEST);
        SortedMap<String, Object> responseInfo = new TreeMap<>(Collections.reverseOrder());
        SeatNo res = new SeatNo(ii + 1, jj + 1, (ii < 4 ? 10 : 8));
        responseInfo.put("returned_ticket", res);
        purchasedSeats[ii][jj] = "available";
        return responseInfo;
    }

    @PostMapping("/purchase")
    public Object purchaseTicket(@RequestBody SeatNo tryNo) {
        if ((tryNo.getRow() < 0 || tryNo.getRow() > totalRows) || (tryNo.getColumn() < 0 || tryNo.getColumn() > totalColumns)) {
            return new ResponseEntity(Map.of("error", "The number of a row or a column is out of bounds!"), HttpStatus.BAD_REQUEST);
        } else if (!"available".equals(purchasedSeats[tryNo.getRow() - 1][tryNo.getColumn() - 1])) {
            return new ResponseEntity(Map.of("error", "The ticket has been already purchased!"), HttpStatus.BAD_REQUEST);
        }

        SortedMap<String, Object> responseInfo = new TreeMap<>(Collections.reverseOrder());
        UUID uuid = new UUID(tryNo.getRow(), tryNo.getColumn());
        SeatNo res = new SeatNo(tryNo.getRow(), tryNo.getColumn(), (tryNo.getRow() <= 4 ? 10 : 8));
        purchasedSeats[tryNo.getRow() - 1][tryNo.getColumn() - 1] = uuid.toString();
        responseInfo.put("token", uuid);
        responseInfo.put("ticket", res);
        return responseInfo;
    }

    @GetMapping("/seats")
    public Map<String, Object> getNumberById() {
        Map<String, Object> mapAvailable = new HashMap<>();
        List<SeatNo> current_seats = new ArrayList<>();

        mapAvailable.put("total_rows", totalRows);
        mapAvailable.put("total_columns", totalColumns);
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if ("available".equals(purchasedSeats[i][j])) {
                    current_seats.add(new SeatNo(i + 1, j + 1, (i < 4 ? 10 : 8)));
                }
            }
        }
        mapAvailable.put("available_seats", current_seats);
        return mapAvailable;
    }
}

class Income {
    private int current_income;
    private int number_of_available_seats;
    private int number_of_purchased_tickets;

    public Income(int number) {
        this.current_income = 0;
        this.number_of_available_seats = number;
        this.number_of_purchased_tickets = 0;
    }

    public int getCurrent_income() {
        return current_income;
    }

    public void setCurrent_income(int current_income) {
        this.current_income = current_income;
    }

    public int getNumber_of_available_seats() {
        return number_of_available_seats;
    }

    public void setNumber_of_available_seats(int number_of_available_seats) {
        this.number_of_available_seats = number_of_available_seats;
    }

    public int getNumber_of_purchased_tickets() {
        return number_of_purchased_tickets;
    }

    public void setNumber_of_purchased_tickets(int number_of_purchased_tickets) {
        this.number_of_purchased_tickets = number_of_purchased_tickets;
    }
}

class TokenNo {
    private String token;
    private int del;

    public TokenNo(String token) {
        this.token = token;
        del = 0;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }
}

class SeatNo {
    private Integer row;
    private Integer column;
    private Integer price;


    public SeatNo() {}

    public SeatNo(Integer row, Integer column, Integer price) {
        this.row = row;
        this.column = column;
        this.price = price;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}
