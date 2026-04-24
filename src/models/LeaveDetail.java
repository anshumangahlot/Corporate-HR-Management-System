package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class LeaveDetail {

    private final LeaveType leaveType;

    protected LeaveDetail(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    protected void executeInsert(Connection con, String sql, Object value, int leaveId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, leaveId);
            ps.setObject(2, value);
            ps.executeUpdate();
        }
    }

    public abstract void save(Connection con, int leaveId) throws SQLException;
}