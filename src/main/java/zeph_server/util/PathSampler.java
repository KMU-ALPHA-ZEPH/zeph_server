package zeph_server.util;

import java.util.ArrayList;
import java.util.List;

public final class PathSampler {

    private PathSampler() {
    }


     // 경로 좌표를 targetSize 개수로 균등 다운샘플링
     // 포인트 수가 targetSize 이하이면 원본을 그대로 반환

    public static <T> List<T> downsample(List<T> points, int targetSize) {
        if (points == null || targetSize <= 1 || points.size() <= targetSize) {
            return points;
        }
        int n = points.size();
        List<T> result = new ArrayList<>(targetSize);
        for (int i = 0; i < targetSize; i++) {
            int idx = (int) Math.round((double) i * (n - 1) / (targetSize - 1));
            result.add(points.get(idx));
        }
        return result;
    }
}
