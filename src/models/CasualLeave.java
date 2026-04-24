package models;

import java.sql.Connection;
import java.sql.SQLException;

public class CasualLeave extends LeaveDetail {

    private final String reason;

    public CasualLeave(String reason) {
        super(LeaveType.CASUAL);
        this.reason = reason;
    }

    @Override
    public void save(Connection con, int leaveId) throws SQLException {
        executeInsert(con, "INSERT INTO Casual_Leave (leave_id, Reason) VALUES (?, ?)", reason, leaveId);
    }
}