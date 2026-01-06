package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;

/**
 *
 * @author rbtuc
 */
public class PartitionsDataTable extends TableView {
    public PartitionsDataTable (PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        super();
        buildTable(app, partitionMap);
    }
    
    private void buildTable(PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        TableColumn<PrimePartition, Number> indexCol = new TableColumn("index");
        TableColumn<PrimePartition, BigDecimal> radianCol = new TableColumn("radian");
        TableColumn<PrimePartition, BigDecimal> countCol = new TableColumn("count");

        getColumns().addAll(indexCol, radianCol, countCol);

        indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
        radianCol.setCellValueFactory(new PropertyValueFactory<>("radian"));
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        List<PrimePartition> partitions = new ArrayList(partitionMap.values());
        Collections.sort(partitions);

        getItems().addAll(partitions);
    }

}
