package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.Constants;

/**
 *
 * @author rbtuc
 */
public class PartitionsDataTable extends TableView {
    public PartitionsDataTable (PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        super();
        buildTable(app, partitionMap);
        setPadding(new Insets(10, 10, 10, 10));
    }
    
    private void buildTable(PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        TableColumn<PrimePartition, BigDecimal> indexCol = new TableColumn("#");
        TableColumn<PrimePartition, BigDecimal> radianCol = new TableColumn("Adjusted Radian");
        TableColumn<PrimePartition, BigDecimal> degreeCol = new TableColumn("Degrees");
        TableColumn<PrimePartition, BigDecimal> prevCol = new TableColumn("Previous Radian");

        TableColumn<PrimePartition, BigDecimal> areaCol = null;

        if (app.getConfig().isUseLogForCounts()) {
            areaCol = new TableColumn("ln(Area)");
        } else {
            areaCol = new TableColumn("Area");
        }

        TableColumn<PrimePartition, BigDecimal> fullRadianCol = new TableColumn("Full Radian");

        TableColumn<PrimePartition, BigDecimal> countCol = null;
        if (app.getConfig().isUseLogForCounts()) {
            countCol = new TableColumn("ln(Count)");
        } else {
            countCol = new TableColumn("Count");
        }

        getColumns().addAll(indexCol, radianCol, degreeCol, prevCol, areaCol, fullRadianCol, countCol);

        indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
        radianCol.setCellValueFactory(new PropertyValueFactory<>("radian"));
        degreeCol.setCellValueFactory(new PropertyValueFactory<>("degrees"));
        prevCol.setCellValueFactory(new PropertyValueFactory<>("previousRadian"));
        areaCol.setCellValueFactory(new PropertyValueFactory<>("originalArea"));
        fullRadianCol.setCellValueFactory(new PropertyValueFactory<>("fullRadian"));

        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        List<PrimePartition> partitions = new ArrayList(partitionMap.values());
        Collections.sort(partitions);

        int indx = 1;
        for (PrimePartition p : partitions) {
            p.setIndex(indx);
            indx++;
        }
        
        getItems().addAll(partitions);
    }

}
