package models;

import java.sql.Connection;
import java.sql.SQLException;

public class SickLeave extends LeaveDetail {

    private final String doctorNote;

    public SickLeave(String doctorNote) {
        super(LeaveType.SICK);
        this.doctorNote = doctorNote;
    }

    @Override
    public void save(Connection con, int leaveId) throws SQLException {
        executeInsert(con, "INSERT INTO Sick_Leave (leave_id, DoctorNote) VALUES (?, ?)", doctorNote, leaveId);
    }
}