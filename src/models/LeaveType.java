package models;

public enum LeaveType {
    SICK("Sick Leave", "Sick_Leave"),
    CASUAL("Casual Leave", "Casual_Leave"),
    PAID("Paid Leave", "Paid_Leave");

    private final String label;
    private final String tableName;

    LeaveType(String label, String tableName) {
        this.label = label;
        this.tableName = tableName;
    }

    public String getLabel() {
        return label;
    }

    public String getTableName() {
        return tableName;
    }

    public static LeaveType fromLabel(String value) {
        for (LeaveType type : values()) {
            if (type.label.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported leave type: " + value);
    }
}