package models;

import java.sql.Connection;
import java.sql.SQLException;

public class PaidLeave extends LeaveDetail {

    private final int balanceUsed;

    public PaidLeave(int balanceUsed) {
        super(LeaveType.PAID);
        this.balanceUsed = balanceUsed;
    }

    @Override
    public void save(Connection con, int leaveId) throws SQLException {
        executeInsert(con, "INSERT INTO Paid_Leave (leave_id, Balance_Used) VALUES (?, ?)", balanceUsed, leaveId);
    }
}