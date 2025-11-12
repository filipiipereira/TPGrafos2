import java.util.*;

public class KCenterExatoOtim {

    private final int[][] dist; // indexado de 1..N
    private final int N;
    private final int K;

    public KCenterExatoOtim(int[][] matrizDistancias, int k) {
        this.dist = matrizDistancias;
        this.N = matrizDistancias.length - 1;
        this.K = k;
    }

    public Result encontrarSolucaoExata() {
        // 1) Collect distinct finite distances
        TreeSet<Integer> values = new TreeSet<>();
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                int d = dist[i][j];
                if (d < Integer.MAX_VALUE / 4) values.add(d);
            }
        }
        // defensive
        if (values.isEmpty()) return new Result(0, Collections.emptyList());

        // 2) binary search over values
        List<Integer> sortedValues = new ArrayList<>(values);
        int lo = 0, hi = sortedValues.size() - 1;
        int bestR = sortedValues.get(hi);
        List<Integer> bestCenters = new ArrayList<>();

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            int R = sortedValues.get(mid);
            // build cover sets for R
            BitSet[] coverByCenter = new BitSet[N + 1]; // index 1..N
            for (int j = 1; j <= N; j++) {
                BitSet bs = new BitSet(N + 1);
                for (int v = 1; v <= N; v++) {
                    if (dist[v][j] <= R) bs.set(v);
                }
                coverByCenter[j] = bs;
            }
            // quick fail if some vertex not coverable by any center
            boolean impossible = false;
            for (int v = 1; v <= N; v++) {
                boolean coverable = false;
                for (int j = 1; j <= N; j++) {
                    if (coverByCenter[j].get(v)) { coverable = true; break; }
                }
                if (!coverable) { impossible = true; break; }
            }
            if (impossible) {
                lo = mid + 1;
                continue;
            }

            // try exact cover with <= K centers
            // Use heuristic ordering for centers (bigger covers first)
            Integer[] centerOrder = new Integer[N];
            for (int j = 0; j < N; j++) centerOrder[j] = j + 1;
            Arrays.sort(centerOrder, (a, b) -> Integer.compare(coverByCenter[b].cardinality(), coverByCenter[a].cardinality()));

            // memo: map from state (BitSet -> remaining k) to boolean
            Map<String, Boolean> memo = new HashMap<>();
            BitSet allUncovered = new BitSet(N + 1);
            for (int v = 1; v <= N; v++) allUncovered.set(v);

            // recursive search returns chosen centers list or null
            List<Integer> solution = coverDfs(allUncovered, K, coverByCenter, centerOrder, memo);
            if (solution != null) {
                // feasible with R: save and search lower
                bestR = R;
                bestCenters = solution;
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        return new Result(bestR, bestCenters);
    }

    private List<Integer> coverDfs(BitSet uncovered, int kleft, BitSet[] coverByCenter, Integer[] centerOrder, Map<String, Boolean> memo) {
        if (uncovered.isEmpty()) return new ArrayList<>();
        if (kleft == 0) return null;

        String key = keyOf(uncovered) + "#" + kleft;
        if (memo.containsKey(key) && Objects.equals(memo.get(key), Boolean.FALSE)) return null;

        // choose vertex with fewest covering centers (MRV)
        int chosenVertex = -1;
        int minOptions = Integer.MAX_VALUE;
        for (int v = uncovered.nextSetBit(1); v >= 0; v = uncovered.nextSetBit(v + 1)) {
            int options = 0;
            for (int c = 1; c <= N; c++) if (coverByCenter[c].get(v)) options++;
            if (options < minOptions) { minOptions = options; chosenVertex = v; }
            if (minOptions <= 1) break;
        }
        if (chosenVertex == -1) return null; // shouldn't happen

        // get centers that cover chosenVertex
        List<Integer> candidates = new ArrayList<>();
        for (int c = 1; c <= N; c++) if (coverByCenter[c].get(chosenVertex)) candidates.add(c);

        // Precompute the coverage (intersection with uncovered) for each candidate once
        if (candidates.size() > 1) {
            List<int[]> pairs = new ArrayList<>(candidates.size()); // pair: {center, count}
            for (int c : candidates) {
                BitSet tmp = (BitSet) coverByCenter[c].clone();
                tmp.and(uncovered);
                pairs.add(new int[]{c, tmp.cardinality()});
            }
            // sort pairs by count descending
            pairs.sort((x, y) -> Integer.compare(y[1], x[1]));
            candidates.clear();
            for (int[] p : pairs) candidates.add(p[0]);
        }

        for (int center : candidates) {
            // new uncovered = uncovered - coverByCenter[center]
            BitSet newUncovered = (BitSet) uncovered.clone();
            newUncovered.andNot(coverByCenter[center]);
            List<Integer> res = coverDfs(newUncovered, kleft - 1, coverByCenter, centerOrder, memo);
            if (res != null) {
                // prepend center and return
                res.add(0, center);
                return res;
            }
        }

        memo.put(key, Boolean.FALSE);
        return null;
    }

    private String keyOf(BitSet bs) {
        // Use the internal long[] representation which is more compact than listing bits
        // and tends to produce fewer temporary objects than iterating set bits.
        long[] arr = bs.toLongArray();
        return Arrays.toString(arr);
    }
}
