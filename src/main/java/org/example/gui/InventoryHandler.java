package org.example.gui;

import com.codehack.nngf.Core.DataModel.DataMap;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.builder.NEvent;
import com.codehack.nngf.gui.Button;
import com.codehack.nngf.gui.ComboBox;
import com.codehack.nngf.gui.Div;
import com.codehack.nngf.gui.NumberField;
import com.codehack.nngf.gui.TextField;
import org.example.Core.DatabaseModule.model.Item;
import org.unbescape.html.HtmlEscape;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class InventoryHandler {
    private static String currentMethod = "ALL";

    public static void handle(NWindow window) {
        bindMethodFilter(window, "inventory-filter-all", "ALL");
        bindMethodFilter(window, "inventory-filter-count", "COUNT");
        bindMethodFilter(window, "inventory-filter-continuous", "CONTINUOUS");

        Button.of(window, "btn-refresh-inventory").onClick(event -> updateView(window));
        Button.of(window, "btn-add-item-modal").onClick(event -> openAddItemModal(window));
        Button.of(window, "modal-close-btn").onClick(event -> closeModal(window));

        Div tbody = Div.of(window, "tbody-inventory");
        tbody.events.addEvent(
                NEvent.CLICK,
                "let exTarget = e.target.closest('.btn-toggle-item-active, .btn-restock-item'); "
                        + "ex = exTarget ? exTarget.classList.contains('btn-toggle-item-active') "
                        + "? 'toggle|' + exTarget.getAttribute('data-id') + '|' + exTarget.getAttribute('data-active') "
                        + ": 'restock|' + exTarget.getAttribute('data-id') + '|' + exTarget.getAttribute('data-name') "
                        + "+ '|' + exTarget.getAttribute('data-stock') + '|' + exTarget.getAttribute('data-method') "
                        + "+ '|' + exTarget.getAttribute('data-price') : null;",
                event -> handleInventoryAction(window, event)
        );

        updateView(window);
    }

    private static void bindMethodFilter(NWindow window, String buttonId, String method) {
        Button.of(window, buttonId).onClick(event -> {
            currentMethod = method;
            setActiveMethodButton(window, buttonId);
            updateView(window);
        });
    }

    private static void setActiveMethodButton(NWindow window, String activeId) {
        String[] ids = {
                "inventory-filter-all", "inventory-filter-count", "inventory-filter-continuous"
        };

        for (String id : ids) {
            Button button = Button.of(window, id);
            if (id.equals(activeId)) button.classList.add("active");
            else button.classList.remove("active");
        }
    }

    public static void updateView(NWindow window) {
        Div tbody = Div.of(window, "tbody-inventory");

        try {
            List<Item> items = HotelApp.databaseModule.getAllItems();
            StringBuilder rows = new StringBuilder();
            int visibleCount = 0;

            for (Item item : items) {
                String method = normalizeMethod(item.getMeasuringMethod());
                if (!"ALL".equals(currentMethod) && !currentMethod.equals(method)) continue;

                visibleCount++;
                boolean active = item.isActive();
                boolean lowStock = item.getStockAmount() <= 20;

                String methodBadge = "COUNT".equals(method)
                        ? "<span class=\"badge badge-count\">COUNT (Units)</span>"
                        : "<span class=\"badge badge-continuous\">CONTINUOUS (kg / L)</span>";

                String stockDisplay = lowStock
                        ? "<strong style=\"color:var(--status-failed-text);\">"
                        + String.format(Locale.US, "%.2f", item.getStockAmount()) + " ⚠️ Low Stock</strong>"
                        : "<strong>" + String.format(Locale.US, "%.2f", item.getStockAmount()) + "</strong>";

                String activeToggle = active
                        ? "<button class=\"toggle-switch-btn active btn-toggle-item-active\" data-id=\"" + item.getItemId()
                        + "\" data-active=\"1\" title=\"Archive item\"><span class=\"toggle-dot active\"></span> Active</button>"
                        : "<button class=\"toggle-switch-btn inactive btn-toggle-item-active\" data-id=\"" + item.getItemId()
                        + "\" data-active=\"0\" title=\"Reactivate item\"><span class=\"toggle-dot inactive\"></span> Inactive</button>";

                String rowStyle = active ? "" : "style=\"opacity:0.7;background-color:#fafafa;\"";

                rows.append("<tr ").append(rowStyle).append(">")
                        .append("<td>#").append(item.getItemId()).append("</td>")
                        .append("<td><strong>").append(escape(item.getName())).append("</strong>")
                        .append(active ? "" : " <span class=\"badge badge-closed\" style=\"margin-left:6px;font-size:0.7rem;\">Archived</span>")
                        .append("</td>")
                        .append("<td>").append(methodBadge).append("</td>")
                        .append("<td>$").append(String.format(Locale.US, "%.2f", item.getPricePerUnit())).append("</td>")
                        .append("<td>").append(stockDisplay).append("</td>")
                        .append("<td>").append(activeToggle).append("</td>")
                        .append("<td><button class=\"btn btn-sm btn-primary btn-restock-item\" "
                        + "data-id=\"" + item.getItemId() + "\" "
                        + "data-name=\"" + escape(item.getName()) + "\" "
                        + "data-stock=\"" + String.format(Locale.US, "%.2f", item.getStockAmount()) + "\" "
                        + "data-method=\"" + method + "\" "
                        + "data-price=\"" + String.format(Locale.US, "%.2f", item.getPricePerUnit()) + "\">Restock</button></td>")
                        .append("</tr>");
            }

            if (visibleCount == 0) {
                tbody.setInnerHTML("<tr><td colspan=\"7\" style=\"text-align:center;padding:2.5rem;color:var(--color-text-muted);\">"
                        + "No items match the selected measuring method.</td></tr>");
            } else {
                tbody.setInnerHTML(rows.toString());
            }
        } catch (SQLException exception) {
            tbody.setInnerHTML("<tr><td colspan=\"7\" style=\"color:red;padding:2rem;\">Unable to load inventory: "
                    + escape(exception.getMessage()) + "</td></tr>");
        }
    }

    private static void handleInventoryAction(NWindow window, Object event) {
        if (event == null) return;
        String[] parts = event.toString().split("\\|", -1);
        if (parts.length < 3) return;

        if ("toggle".equals(parts[0])) {
            toggleItem(window, parts);
        } else if ("restock".equals(parts[0]) && parts.length == 6) {
            openRestockModal(window, parts);
        }
    }

    private static void toggleItem(NWindow window, String[] parts) {
        int itemId;
        try {
            itemId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            HotelApp.showToast(window, "Invalid item action.", "error");
            return;
        }

        boolean currentlyActive = "1".equals(parts[2]);
        try {
            Item item = HotelApp.databaseModule.getItemById(itemId);
            if (item == null) {
                HotelApp.showToast(window, "Item was not found.", "error");
                return;
            }

            item.setIsActive(!currentlyActive);
            HotelApp.databaseModule.updateItem(item);
            HotelApp.showToast(window,
                    item.isActive() ? "Item reactivated." : "Item archived. Historical bills are preserved.",
                    "success");
            updateView(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to update item status: " + exception.getMessage(), "error");
        }
    }

    private static void openAddItemModal(NWindow window) {
        Div modal = Div.of(window, "modal-container");
        Div title = Div.of(window, "modal-title");
        Div body = Div.of(window, "modal-body");
        Div footer = Div.of(window, "modal-footer");

        title.setInnerText("Add New Supply / Order Item");
        body.setInnerHTML("<div class=\"form-group\"><label class=\"form-label\" for=\"add-item-name\">Item Name / Description:</label>"
                + "<input type=\"text\" id=\"add-item-name\" class=\"form-control\" maxlength=\"100\" placeholder=\"e.g. Mineral Water 500ml\" required></div>"
                + "<div class=\"form-group\"><label class=\"form-label\" for=\"add-item-method\">Measuring Method:</label>"
                + "<select id=\"add-item-method\" class=\"form-control\"><option value=\"COUNT\" selected>COUNT (Discrete units / pieces)</option>"
                + "<option value=\"CONTINUOUS\">CONTINUOUS (Decimal kg / liters)</option></select></div>"
                + "<div class=\"form-group\"><label class=\"form-label\" for=\"add-item-price\">Price Per Unit ($, >= 0):</label>"
                + "<input type=\"number\" id=\"add-item-price\" class=\"form-control\" step=\"0.01\" min=\"0\" value=\"5.00\" required></div>"
                + "<div class=\"form-group\"><label class=\"form-label\" for=\"add-item-stock\">Initial Stock Amount (>= 0):</label>"
                + "<input type=\"number\" id=\"add-item-stock\" class=\"form-control\" step=\"1\" min=\"0\" value=\"50\" required></div>");

        footer.setInnerHTML("<button id=\"btn-modal-cancel\" class=\"btn btn-outline btn-sm\">Cancel</button>"
                + "<button id=\"btn-modal-confirm-add-item\" class=\"btn btn-primary btn-sm\">Add Item</button>");

        modal.classList.remove("hidden");
        Button.of(window, "btn-modal-cancel").onClick(event -> closeModal(window));

        TextField nameField = TextField.of(window, "add-item-name");
        ComboBox methodField = ComboBox.of(window, "add-item-method");
        NumberField priceField = new NumberField("add-item-price", window);
        NumberField stockField = new NumberField("add-item-stock", window);

        Button.of(window, "btn-modal-confirm-add-item").events.addEvent(
                NEvent.CLICK,
                event -> createItemFromModal(window, event),
                nameField.q("name").value(),
                methodField.q("method").value(),
                priceField.q("price").value(),
                stockField.q("stock").value()
        );

        methodField.events.addJsEvent(NEvent.CHANGE,
                "let stock = this.parentElement.querySelector('#add-item-stock'); stock.step = this.value === 'COUNT' ? '1' : '0.01'; "
                        + "if (this.value === 'COUNT') stock.value = String(Math.round(Number(stock.value) || 0));");
    }

    private static void createItemFromModal(NWindow window, DataMap event) {
        if (event == null) return;

        String name = event.GetString("name");
        String method = event.GetString("method");
        String priceText = event.GetString("price");
        String stockText = event.GetString("stock");

        name = name == null ? "" : name.trim();
        method = method == null ? "" : method.trim().toUpperCase(Locale.ROOT);
        double price;
        double stock;

        try {
            price = Double.parseDouble(priceText == null ? "" : priceText);
            stock = Double.parseDouble(stockText == null ? "" : stockText);
        } catch (NumberFormatException exception) {
            HotelApp.showToast(window, "Price and stock must be valid numbers.", "error");
            return;
        }

        if (name.isEmpty()) {
            HotelApp.showToast(window, "Item name is required.", "error");
            return;
        }
        if (!"COUNT".equals(method) && !"CONTINUOUS".equals(method)) {
            HotelApp.showToast(window, "Measuring method must be COUNT or CONTINUOUS.", "error");
            return;
        }
        if (price < 0) {
            HotelApp.showToast(window, "Price per unit must be greater than or equal to 0.", "error");
            return;
        }
        if (stock < 0) {
            HotelApp.showToast(window, "Stock amount must be greater than or equal to 0.", "error");
            return;
        }
        if ("COUNT".equals(method) && Math.abs(stock - Math.rint(stock)) > 0.000001) {
            HotelApp.showToast(window, "COUNT items require a whole-number stock amount.", "error");
            return;
        }

        try {
            Item item = new Item(name, price, stock, method);
            int itemId = HotelApp.databaseModule.addNewItem(item);
            if (itemId <= 0) {
                HotelApp.showToast(window, "Failed to create item.", "error");
                return;
            }

            closeModal(window);
            HotelApp.showToast(window, "Item \"" + name + "\" created successfully!", "success");
            updateView(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to add item: " + exception.getMessage(), "error");
        }
    }

    private static void openRestockModal(NWindow window, String[] parts) {
        int itemId;
        double currentStock;
        double currentPrice;

        try {
            itemId = Integer.parseInt(parts[1]);
            currentStock = Double.parseDouble(parts[3]);
            currentPrice = Double.parseDouble(parts[5]);
        } catch (NumberFormatException exception) {
            HotelApp.showToast(window, "Invalid restock data.", "error");
            return;
        }

        String name = parts[2].replace("&#124;", "|");
        String method = parts[4];
        boolean count = "COUNT".equals(method);

        Div modal = Div.of(window, "modal-container");
        Div title = Div.of(window, "modal-title");
        Div body = Div.of(window, "modal-body");
        Div footer = Div.of(window, "modal-footer");

        title.setInnerText("Restock: " + name);
        body.setInnerHTML("<div style=\"background:var(--color-surface-alt);padding:10px 14px;border-radius:6px;margin-bottom:1rem;\">"
                + "<div>Current Stock: <strong>" + String.format(Locale.US, "%.2f", currentStock) + "</strong> (" + method + ")</div>"
                + "<div>Current Unit Price: <strong>$" + String.format(Locale.US, "%.2f", currentPrice) + "</strong></div></div>"
                + "<div class=\"form-group\"><label class=\"form-label\" for=\"restock-delta\">Quantity to Add:</label>"
                + "<input type=\"number\" id=\"restock-delta\" class=\"form-control\" step=\"" + (count ? "1" : "0.01")
                + "\" min=\"" + (count ? "1" : "0.01") + "\" value=\"" + (count ? "10" : "5.00") + "\" required></div>"
                + "<div class=\"form-group\"><label class=\"form-label\" for=\"restock-price\">Update Price Per Unit (optional):</label>"
                + "<input type=\"number\" id=\"restock-price\" class=\"form-control\" step=\"0.01\" min=\"0\" value=\""
                + String.format(Locale.US, "%.2f", currentPrice) + "\" required></div>");

        footer.setInnerHTML("<button id=\"btn-modal-cancel\" class=\"btn btn-outline btn-sm\">Cancel</button>"
                + "<button id=\"btn-modal-confirm-restock\" class=\"btn btn-primary btn-sm\">Apply Restock</button>");

        modal.classList.remove("hidden");
        Button.of(window, "btn-modal-cancel").onClick(event -> closeModal(window));

        NumberField deltaField = new NumberField("restock-delta", window);
        NumberField priceField = new NumberField("restock-price", window);

        Button.of(window, "btn-modal-confirm-restock").events.addEvent(
                NEvent.CLICK,
                event -> applyRestock(window, itemId, event),
                deltaField.q("delta").value(),
                priceField.q("price").value()
        );
    }

    private static void applyRestock(NWindow window, int itemId, DataMap event) {
        if (event == null) return;

        String deltaText = event.GetString("delta");
        String priceText = event.GetString("price");
        double delta;
        double newPrice;

        try {
            delta = Double.parseDouble(deltaText == null ? "" : deltaText);
            newPrice = Double.parseDouble(priceText == null ? "" : priceText);
        } catch (NumberFormatException exception) {
            HotelApp.showToast(window, "Restock values must be valid numbers.", "error");
            return;
        }

        if (delta <= 0) {
            HotelApp.showToast(window, "Restock quantity must be greater than 0.", "error");
            return;
        }
        if (newPrice < 0) {
            HotelApp.showToast(window, "Price per unit must be greater than or equal to 0.", "error");
            return;
        }

        try {
            Item item = HotelApp.databaseModule.getItemById(itemId);
            if (item == null) {
                HotelApp.showToast(window, "Item was not found.", "error");
                return;
            }

            String method = item.getMeasuringMethod();
            if ("COUNT".equalsIgnoreCase(method) && Math.abs(delta - Math.rint(delta)) > 0.000001) {
                HotelApp.showToast(window, "COUNT items require a whole-number restock quantity.", "error");
                return;
            }

            double currentStock = item.getStockAmount();
            double newStock = currentStock + delta;
            if (newStock < 0) {
                HotelApp.showToast(window, "Stock cannot be negative.", "error");
                return;
            }

            HotelApp.databaseModule.updateItemStock(itemId, newStock);
            if (Math.abs(newPrice - item.getPricePerUnit()) > 0.000001) {
                item.setStockAmount(newStock);
                item.setPricePerUnit(newPrice);
                HotelApp.databaseModule.updateItem(item);
            }

            closeModal(window);
            HotelApp.showToast(window, "Item restocked successfully!", "success");
            updateView(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to restock item: " + exception.getMessage(), "error");
        }
    }

    private static void closeModal(NWindow window) {
        Div modal = Div.of(window, "modal-container");
        modal.classList.add("hidden");
        Div.of(window, "modal-body").setInnerHTML("");
        Div.of(window, "modal-footer").setInnerHTML("");
    }

    private static String normalizeMethod(String method) {
        return "CONTINUOUS".equalsIgnoreCase(method) ? "CONTINUOUS" : "COUNT";
    }

    private static String escape(String value) {
        return value == null ? "" : HtmlEscape.escapeHtml5(value);
    }
}
