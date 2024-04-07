Process records in parallel.
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransactionProcessor {

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        ConcurrentHashMap<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public List<TxnData> filterDuplicatesParallel(List<TxnData> transactions) {
        return transactions.parallelStream()
                .filter(distinctByKey(TxnData::getTxnId))
                .collect(Collectors.toList());
    }
}

class TxnData {
    private String txnId;
    // Other fields...

    public String getTxnId() {
        return this.txnId;
    }
    // Constructors, getters, and setters...
}
