package com.hashvis.model.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table {
  private final List<Row> rows = new ArrayList<>();

  // Index of the currently selected row, or -1 if none.
  private int currentRow = -1;

  public Table(int size) {
    if (size <= 0)
      throw new IllegalArgumentException("Table size must be positive: " + size);
    for (int i = 0; i < size; i++)
      rows.add(new Row(i));
  }

  // Selects the row at the given index for visualization: de-highlights any
  // previously selected row, then highlights and returns the new one.
  public Row getRow(int index) {
    int target = (index % size() + size()) % size();
    if (currentRow != -1 && currentRow != target)
      rows.get(currentRow).unchoose();
    currentRow = target;
    Row row = rows.get(target);
    row.choose();
    return row;
  }

  public int size() {
    return rows.size();
  }

  public void reset() {
    currentRow = -1;
    for (Row row : rows)
      row.reset();
  }

  public List<Row> getRows() {
    return Collections.unmodifiableList(rows);
  }
}
