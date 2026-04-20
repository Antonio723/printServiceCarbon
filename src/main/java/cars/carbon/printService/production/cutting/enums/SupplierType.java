package cars.carbon.printService.production.cutting.enums;

public enum SupplierType {
    OPERA("Opera"),
    COMTEC("Comtec"),
    PROTECTA("Protecta");

    private final String description;

    SupplierType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static SupplierType fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String trimmedText = text.trim();

        // Primeiro tenta pelo nome do enum (OPERA, COMTEC)
        for (SupplierType supplier : SupplierType.values()) {
            if (supplier.name().equalsIgnoreCase(trimmedText)) {
                return supplier;
            }
        }

        // Depois tenta pela descrição (Opera, Comtec)
        for (SupplierType supplier : SupplierType.values()) {
            if (supplier.getDescription().equalsIgnoreCase(trimmedText)) {
                return supplier;
            }
        }

        return null;
    }

    public static boolean isValid(String text) {
        return fromString(text) != null;
    }

    @Override
    public String toString() {
        return this.description;
    }
}