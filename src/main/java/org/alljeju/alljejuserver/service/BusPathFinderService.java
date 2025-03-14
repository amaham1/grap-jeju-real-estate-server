package org.alljeju.alljejuserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.mapper.BusRouteMapper;
import org.alljeju.alljejuserver.mapper.BusStationMapper;
import org.alljeju.alljejuserver.mapper.BusStationRouteMapper;
import org.alljeju.alljejuserver.model.BusRoute;
import org.alljeju.alljejuserver.model.BusStation;
import org.alljeju.alljejuserver.model.BusStationRoute;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 버스 경로 탐색 서비스
 * 다익스트라(Dijkstra) 알고리즘을 활용한 최적 경로 탐색
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusPathFinderService {
    private final BusStationMapper busStationMapper;
    private final BusRouteMapper busRouteMapper;
    private final BusStationRouteMapper busStationRouteMapper;
    
    // 정류장 및 노선 정보 캐싱을 위한 맵 추가
    private final Map<String, String> stationNameCache = new HashMap<>();
    private final Map<String, BusRoute> routeInfoCache = new HashMap<>();
    
    // 직통 노선 캐시 (출발지_도착지 -> 노선 정보 리스트)
    private final Map<String, List<BusStationRoute>> directRoutesCache = new HashMap<>();
    // 노선별 정류장 순서 캐시 (routeId_updnDir -> 정류장 목록)
    private final Map<String, List<BusStationRoute>> routeStopsCache = new HashMap<>();
    
    // 탐색 알고리즘의 최대 환승 횟수 설정
    private static final int MAX_TRANSFERS = 5;
    
    // 스프링 빈 초기화 시 캐시 구축
    @PostConstruct
    public void initializeCache() {
        log.info("버스 경로 캐시 초기화 시작");
        
        // 1. 모든 노선 조회
        List<BusRoute> allRoutes = busRouteMapper.findAllRoutes();
        for (BusRoute route : allRoutes) {
            routeInfoCache.put(route.getRouteId(), route);
            
            // 상행 노선 정류장 정보 조회 및 캐싱
            List<BusStationRoute> upStops = busStationRouteMapper.findByRouteIdAndUpdnDirAndUseYnOrderByStationOrdAsc(route.getRouteId(), "0", "Y");
            
            // 하행 노선 정류장 정보 조회 및 캐싱
            List<BusStationRoute> downStops = busStationRouteMapper.findByRouteIdAndUpdnDirAndUseYnOrderByStationOrdAsc(route.getRouteId(), "1", "Y");
            
            routeStopsCache.put(route.getRouteId() + "_0", upStops);
            routeStopsCache.put(route.getRouteId() + "_1", downStops);
            
            // 직통 노선 정보 캐싱
            cacheDirectRoutes(upStops);
            cacheDirectRoutes(downStops);
        }
        
        // 2. 모든 정류장 조회 및 이름 캐싱
        List<BusStation> allStations = busStationMapper.findAllStations();
        for (BusStation station : allStations) {
            stationNameCache.put(station.getStationId(), station.getStationNm());
        }
        
        log.info("버스 경로 캐시 초기화 완료: {} 노선, {} 직통경로, {} 정류장", 
            routeStopsCache.size()/2, directRoutesCache.size(), stationNameCache.size());
    }

    // 직통 노선 정보 캐싱 헬퍼 메서드
    private void cacheDirectRoutes(List<BusStationRoute> stops) {
        if (stops == null || stops.size() < 2) return;
        
        // 모든 가능한 출발지-도착지 쌍에 대해 직통 노선 정보 저장
        for (int i = 0; i < stops.size(); i++) {
            String sourceId = stops.get(i).getStationId();
            
            for (int j = i + 1; j < stops.size(); j++) {
                String targetId = stops.get(j).getStationId();
                String key = sourceId + "_" + targetId;
                
                List<BusStationRoute> routes = directRoutesCache.computeIfAbsent(key, k -> new ArrayList<>());
                routes.add(stops.get(i)); // 출발 정류장의 노선 정보 저장
            }
        }
    }

    /**
     * 출발 정류장에서 도착 정류장까지 도달 가능한지 여부와 경로 정보 제공
     * (시간, 거리, 환승 횟수 등을 고려하지 않고 가능한 빠른 경로 탐색)
     * 
     * @param source 출발 정류장 ID
     * @param target 도착 정류장 ID
     * @return 도달 가능 여부와 관련 정보를 포함한 Map (reachable, path, routes 등)
     */
    public Map<String, Object> isReachable(String source, String target) {
        long startTime = System.currentTimeMillis();
        log.info("정류장 간 도달 가능성 확인: {} → {}", source, target);
        
        Map<String, Object> result = new HashMap<>();
        result.put("startStationId", source);
        result.put("destinationStationId", target);
        
        // 같은 정류장인 경우 바로 true 반환
        if (source.equals(target)) {
            result.put("reachable", true);
            result.put("needTransfer", false);
            result.put("transferCount", 0);
            result.put("reason", "출발지와 도착지가 동일합니다.");
            result.put("path", Collections.singletonList(Map.of(
                "stationId", source,
                "stationName", getStationName(source),
                "transfer", false
            )));
            return result;
        }
        
        // 1. 직통 노선 확인 (캐시에서 바로 검색)
        String directKey = source + "_" + target;
        List<BusStationRoute> directRoutes = directRoutesCache.get(directKey);
        
        if (directRoutes != null && !directRoutes.isEmpty()) {
            // 첫 번째 직통 노선 정보 사용
            BusStationRoute route = directRoutes.get(0);
            String routeId = route.getRouteId();
            
            // 직통 경로 정보 구성
            List<Map<String, Object>> pathInfo = new ArrayList<>(2);
            
            // 출발 정류장
            Map<String, Object> startInfo = new HashMap<>(3);
            startInfo.put("stationId", source);
            startInfo.put("stationName", getStationName(source));
            startInfo.put("transfer", false);
            pathInfo.add(startInfo);
            
            // 노선 정보
            Map<String, Object> routeInfo = new HashMap<>(3);
            routeInfo.put("routeId", routeId);
            routeInfo.put("routeName", getRouteName(routeId));
            routeInfo.put("routeType", getRouteType(routeId));
            
            // 도착 정류장
            Map<String, Object> destInfo = new HashMap<>(5);
            destInfo.put("stationId", target);
            destInfo.put("stationName", getStationName(target));
            destInfo.put("transfer", false);
            destInfo.put("routeId", routeId);
            destInfo.put("routeName", getRouteName(routeId));
            pathInfo.add(destInfo);
            
            result.put("reachable", true);
            result.put("needTransfer", false);
            result.put("transferCount", 0);
            result.put("usedRoutes", Collections.singletonList(routeInfo));
            result.put("reason", "직통 노선이 존재합니다: " + getRouteName(routeId));
            result.put("path", pathInfo);
            
            long endTime = System.currentTimeMillis();
            log.info("직통 노선 찾음. 소요시간: {}ms", (endTime - startTime));
            return result;
        }
        
        // 2. 양방향 BFS(Bidirectional BFS)를 사용한 환승 경로 탐색
        // 정방향 탐색(출발→도착)과 역방향 탐색(도착→출발)을 동시에 수행하여 중간에서 만나는 경로 찾기
        Map<String, PathState> forwardVisited = new HashMap<>();     // 출발지에서 시작하는 정방향 방문 정보
        Map<String, PathState> backwardVisited = new HashMap<>();    // 도착지에서 시작하는 역방향 방문 정보
        
        Queue<PathState> forwardQueue = new LinkedList<>();          // 정방향 BFS 큐
        Queue<PathState> backwardQueue = new LinkedList<>();         // 역방향 BFS 큐
        
        // 만나는 지점과 관련 정보 저장 변수
        String meetingPoint = null;
        PathState forwardState = null;
        PathState backwardState = null;
        
        // 출발 정류장에서 출발하는 모든 노선 찾아서 정방향 큐에 추가
        for (BusStationRoute sr : busStationRouteMapper.findByStationIdAndUseYn(source, "Y")) {
            PathState initialState = new PathState();
            initialState.stationId = source;
            initialState.routeId = sr.getRouteId();
            initialState.updnDir = sr.getUpdnDir();
            initialState.transferCount = 0;
            initialState.prevState = null;
            initialState.isTransfer = false;
            
            // 방문 키 생성
            String visitKey = source + "_" + sr.getRouteId() + "_" + sr.getUpdnDir();
            forwardVisited.put(visitKey, initialState);
            forwardQueue.add(initialState);
        }
        
        // 도착 정류장에서 출발하는 모든 노선 찾아서 역방향 큐에 추가
        for (BusStationRoute sr : busStationRouteMapper.findByStationIdAndUseYn(target, "Y")) {
            PathState initialState = new PathState();
            initialState.stationId = target;
            initialState.routeId = sr.getRouteId();
            initialState.updnDir = sr.getUpdnDir();
            initialState.transferCount = 0;
            initialState.prevState = null;
            initialState.isTransfer = false;
            
            // 방문 키 생성
            String visitKey = target + "_" + sr.getRouteId() + "_" + sr.getUpdnDir();
            backwardVisited.put(visitKey, initialState);
            backwardQueue.add(initialState);
        }
        
        // 양방향 BFS 시작
        int iteration = 0;
        int maxIteration = 1000;  // 무한 루프 방지를 위한 최대 반복 횟수
        
        while (!forwardQueue.isEmpty() && !backwardQueue.isEmpty() && iteration < maxIteration) {
            iteration++;
            
            // 1. 정방향 BFS 한 단계 진행
            if (!forwardQueue.isEmpty()) {
                PathState currentState = forwardQueue.poll();
                String currentStationId = currentState.stationId;
                String currentRouteId = currentState.routeId;
                String currentUpdnDir = currentState.updnDir;
                
                // 현재 정류장이 목적지인 경우 바로 경로 반환
                if (currentStationId.equals(target)) {
                    result.put("reachable", true);
                    result.put("needTransfer", currentState.transferCount > 0);
                    result.put("transferCount", currentState.transferCount);
                    
                    // 경로 및 노선 정보 구성
                    List<Map<String, Object>> finalPath = reconstructPath(currentState, false);
                    List<Map<String, Object>> finalUsedRoutes = reconstructUsedRoutes(currentState, false);
                    
                    result.put("path", finalPath);
                    result.put("usedRoutes", finalUsedRoutes);
                    
                    long endTime = System.currentTimeMillis();
                    result.put("timeTaken", endTime - startTime);
                    
                    return result;
                }
                
                // 역방향 탐색과 만나는지 확인 (양방향 BFS의 핵심)
                for (Map.Entry<String, PathState> entry : backwardVisited.entrySet()) {
                    String[] parts = entry.getKey().split("_");
                    String bwStationId = parts[0];
                    
                    if (bwStationId.equals(currentStationId)) {
                        // 정방향과 역방향이 만나는 지점 발견
                        meetingPoint = currentStationId;
                        forwardState = currentState;
                        backwardState = entry.getValue();
                        break;
                    }
                }
                
                if (meetingPoint != null) {
                    break;  // 만나는 지점을 찾았으면 BFS 종료
                }
                
                // 최대 환승 횟수 초과 시 더 이상 탐색하지 않음
                if (currentState.transferCount > MAX_TRANSFERS) {
                    continue;
                }
                
                // 현재 노선의 다음 정류장들 탐색
                String routeStopsCacheKey = currentRouteId + "_" + currentUpdnDir;
                List<BusStationRoute> routeStops = routeStopsCache.get(routeStopsCacheKey);
                
                int currentIdx = indexOf(routeStops, currentStationId);
                if (currentIdx == -1) continue;
                
                // 정방향 탐색: 인덱스가 큰 방향으로 순차적으로 탐색
                for (int i = currentIdx + 1; i < routeStops.size(); i++) {
                    BusStationRoute nextStop = routeStops.get(i);
                    String nextStationId = nextStop.getStationId();
                    
                    // 현재 노선으로 다음 정류장 방문
                    String nextVisitKey = nextStationId + "_" + currentRouteId + "_" + currentUpdnDir;
                    if (!forwardVisited.containsKey(nextVisitKey)) {
                        // 다음 정류장을 현재 노선으로 계속 이동
                        PathState nextState = new PathState();
                        nextState.stationId = nextStationId;
                        nextState.routeId = currentRouteId;
                        nextState.updnDir = currentUpdnDir;
                        nextState.transferCount = currentState.transferCount;
                        nextState.prevState = currentState;
                        nextState.isTransfer = false;
                        
                        forwardQueue.add(nextState);
                        forwardVisited.put(nextVisitKey, nextState);
                        
                        // 다른 노선으로 환승 가능성 확인
                        if (currentState.transferCount < MAX_TRANSFERS) {
                            List<BusStationRoute> transferCandidates = busStationRouteMapper.findByStationIdAndUseYn(nextStationId, "Y");
                            for (BusStationRoute transferRoute : transferCandidates) {
                                // 같은 노선은 제외 (이미 처리됨)
                                if (transferRoute.getRouteId().equals(currentRouteId) && transferRoute.getUpdnDir().equals(currentUpdnDir)) {
                                    continue;
                                }
                                
                                // 환승 노선 방문 처리
                                String transferVisitKey = nextStationId + "_" + transferRoute.getRouteId() + "_" + transferRoute.getUpdnDir();
                                if (!forwardVisited.containsKey(transferVisitKey)) {
                                    PathState transferState = new PathState();
                                    transferState.stationId = nextStationId;
                                    transferState.routeId = transferRoute.getRouteId();
                                    transferState.updnDir = transferRoute.getUpdnDir();
                                    transferState.transferCount = currentState.transferCount + 1;
                                    transferState.prevState = currentState;
                                    transferState.isTransfer = true;
                                    
                                    forwardQueue.add(transferState);
                                    forwardVisited.put(transferVisitKey, transferState);
                                }
                            }
                        }
                    }
                }
            }
            
            // 2. 역방향 BFS 한 단계 진행
            if (!backwardQueue.isEmpty()) {
                PathState currentState = backwardQueue.poll();
                String currentStationId = currentState.stationId;
                String currentRouteId = currentState.routeId;
                String currentUpdnDir = currentState.updnDir;
                
                // 현재 정류장이 출발지인 경우 바로 경로 반환
                if (currentStationId.equals(source)) {
                    result.put("reachable", true);
                    result.put("needTransfer", currentState.transferCount > 0);
                    result.put("transferCount", currentState.transferCount);
                    
                    // 경로 및 노선 정보 구성 (역방향이므로 뒤집어야 함)
                    List<Map<String, Object>> finalPath = reconstructPath(currentState, true);
                    List<Map<String, Object>> finalUsedRoutes = reconstructUsedRoutes(currentState, true);
                    
                    result.put("path", finalPath);
                    result.put("usedRoutes", finalUsedRoutes);
                    
                    long endTime = System.currentTimeMillis();
                    result.put("timeTaken", endTime - startTime);
                    
                    return result;
                }
                
                // 정방향 탐색과 만나는지 확인 (이미 위에서 체크했으므로 중복 체크 필요 없음)
                if (meetingPoint != null) {
                    break;
                }
                
                // 최대 환승 횟수 초과 시 더 이상 탐색하지 않음
                if (currentState.transferCount > MAX_TRANSFERS) {
                    continue;
                }
                
                // 현재 노선의 이전 정류장들 탐색 (역방향 탐색)
                String routeStopsCacheKey = currentRouteId + "_" + currentUpdnDir;
                List<BusStationRoute> routeStops = routeStopsCache.get(routeStopsCacheKey);
                
                int currentIdx = indexOf(routeStops, currentStationId);
                if (currentIdx == -1) continue;
                
                // 역방향 탐색: 인덱스가 작은 방향으로 역순으로 탐색
                for (int i = currentIdx - 1; i >= 0; i--) {
                    BusStationRoute prevStop = routeStops.get(i);
                    String prevStationId = prevStop.getStationId();
                    
                    // 현재 노선으로 이전 정류장 방문
                    String prevVisitKey = prevStationId + "_" + currentRouteId + "_" + currentUpdnDir;
                    if (!backwardVisited.containsKey(prevVisitKey)) {
                        // 이전 정류장을 현재 노선으로 계속 이동
                        PathState prevState = new PathState();
                        prevState.stationId = prevStationId;
                        prevState.routeId = currentRouteId;
                        prevState.updnDir = currentUpdnDir;
                        prevState.transferCount = currentState.transferCount;
                        prevState.prevState = currentState;
                        prevState.isTransfer = false;
                        
                        backwardQueue.add(prevState);
                        backwardVisited.put(prevVisitKey, prevState);
                        
                        // 다른 노선으로 환승 가능성 확인
                        if (currentState.transferCount < MAX_TRANSFERS) {
                            List<BusStationRoute> transferCandidates = busStationRouteMapper.findByStationIdAndUseYn(prevStationId, "Y");
                            for (BusStationRoute transferRoute : transferCandidates) {
                                // 같은 노선은 제외 (이미 처리됨)
                                if (transferRoute.getRouteId().equals(currentRouteId) && transferRoute.getUpdnDir().equals(currentUpdnDir)) {
                                    continue;
                                }
                                
                                // 환승 노선 방문 처리
                                String transferVisitKey = prevStationId + "_" + transferRoute.getRouteId() + "_" + transferRoute.getUpdnDir();
                                if (!backwardVisited.containsKey(transferVisitKey)) {
                                    PathState transferState = new PathState();
                                    transferState.stationId = prevStationId;
                                    transferState.routeId = transferRoute.getRouteId();
                                    transferState.updnDir = transferRoute.getUpdnDir();
                                    transferState.transferCount = currentState.transferCount + 1;
                                    transferState.prevState = currentState;
                                    transferState.isTransfer = true;
                                    
                                    backwardQueue.add(transferState);
                                    backwardVisited.put(transferVisitKey, transferState);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // 양방향 BFS에서 만나는 지점이 있는 경우, 경로 합치기
        if (meetingPoint != null && forwardState != null && backwardState != null) {
            // 경로 및 노선 정보 구성 (양방향에서 만나는 지점)
            List<Map<String, Object>> forwardPath = reconstructPath(forwardState, false);
            List<Map<String, Object>> backwardPath = reconstructPath(backwardState, true);
            
            // 중복 정류장 제거 (만나는 지점은 한 번만 포함)
            backwardPath.remove(0);  // 역방향 경로의 첫 번째 정류장 제거 (중복)
            
            // 전체 경로 합치기
            List<Map<String, Object>> combinedPath = new ArrayList<>(forwardPath);
            combinedPath.addAll(backwardPath);
            
            // 사용된 노선 정보 합치기
            List<Map<String, Object>> forwardRoutes = reconstructUsedRoutes(forwardState, false);
            List<Map<String, Object>> backwardRoutes = reconstructUsedRoutes(backwardState, true);
            
            // 중복 노선 제거
            Set<String> routeIds = new HashSet<>();
            List<Map<String, Object>> combinedRoutes = new ArrayList<>();
            
            for (Map<String, Object> route : forwardRoutes) {
                String routeId = (String) route.get("routeId");
                if (!routeIds.contains(routeId)) {
                    routeIds.add(routeId);
                    combinedRoutes.add(route);
                }
            }
            
            for (Map<String, Object> route : backwardRoutes) {
                String routeId = (String) route.get("routeId");
                if (!routeIds.contains(routeId)) {
                    routeIds.add(routeId);
                    combinedRoutes.add(route);
                }
            }
            
            result.put("reachable", true);
            result.put("needTransfer", true);
            result.put("transferCount", forwardState.transferCount + backwardState.transferCount);
            result.put("path", combinedPath);
            result.put("usedRoutes", combinedRoutes);
            
            long endTime = System.currentTimeMillis();
            result.put("timeTaken", endTime - startTime);
            
            return result;
        }
        
        // 도달 불가능한 경우
        result.put("reachable", false);
        result.put("needTransfer", false);
        result.put("transferCount", 0);
        result.put("reason", "출발지와 도착지를 연결하는 경로를 찾을 수 없습니다.");
        result.put("path", Collections.emptyList());
        result.put("usedRoutes", Collections.emptyList());
        
        long endTime = System.currentTimeMillis();
        result.put("timeTaken", endTime - startTime);
        return result;
    }
    
    /**
     * 경로 상태로부터 전체 경로 정보를 생성
     * 
     * @param finalState 최종 경로 상태
     * @param isBackward 역방향 여부
     * @return 경로 정보 리스트
     */
    private List<Map<String, Object>> reconstructPath(PathState finalState, boolean isBackward) {
        List<PathState> states = new ArrayList<>();
        
        // 상태 역추적
        PathState current = finalState;
        while (current != null) {
            states.add(current);
            current = current.prevState;
        }
        
        // 역방향이면 그대로 사용, 정방향이면 역순으로 뒤집기
        if (!isBackward) {
            Collections.reverse(states);
        }
        
        // 경로 생성
        List<Map<String, Object>> path = new ArrayList<>(states.size());
        
        for (PathState state : states) {
            Map<String, Object> node = new HashMap<>(4);
            node.put("stationId", state.stationId);
            node.put("stationName", getStationName(state.stationId));
            
            // 환승 여부 설정
            node.put("transfer", state.isTransfer);
            
            // 현재 노선 정보 추가
            node.put("routeId", state.routeId);
            node.put("routeName", getRouteName(state.routeId));
            
            path.add(node);
        }
        
        return path;
    }
    
    /**
     * 경로 상태로부터 사용된 노선 정보를 생성
     * 
     * @param finalState 최종 경로 상태
     * @param isBackward 역방향 여부
     * @return 사용된 노선 정보 리스트
     */
    private List<Map<String, Object>> reconstructUsedRoutes(PathState finalState, boolean isBackward) {
        List<PathState> states = new ArrayList<>();
        Map<String, PathState> routeMap = new LinkedHashMap<>();
        
        // 상태 역추적하면서 사용된 노선 정보 수집
        PathState current = finalState;
        while (current != null) {
            states.add(current);
            if (current.routeId != null && !routeMap.containsKey(current.routeId)) {
                routeMap.put(current.routeId, current);
            }
            current = current.prevState;
        }
        
        // 노선 정보 생성
        List<Map<String, Object>> usedRoutes = new ArrayList<>(routeMap.size());
        
        // 역방향이면 역순으로 뒤집기
        List<String> routeKeys = new ArrayList<>(routeMap.keySet());
        if (isBackward) {
            Collections.reverse(routeKeys);
        }
        
        for (String routeId : routeKeys) {
            Map<String, Object> routeInfo = new HashMap<>(4);
            routeInfo.put("routeId", routeId);
            routeInfo.put("routeName", getRouteName(routeId));
            routeInfo.put("routeType", getRouteType(routeId));
            usedRoutes.add(routeInfo);
        }
        
        return usedRoutes;
    }
    
    /**
     * 노선 유형 조회
     * @param routeId 노선 ID
     * @return 노선 유형 (간선, 지선 등)
     */
    private String getRouteType(String routeId) {
        BusRoute route = getRouteInfo(routeId);
        return route != null ? route.getRouteType() : "알 수 없음";
    }
    
    /**
     * 노선 정보 조회 (캐싱 적용)
     * @param routeId 노선 ID
     * @return 노선 정보 객체
     */
    private BusRoute getRouteInfo(String routeId) {
        return routeInfoCache.computeIfAbsent(routeId, id -> 
            busRouteMapper.findByRouteId(id));
    }

    // Helper: stationId에 해당하는 정류소의 이름 조회
    private String getStationName(String stationId) {
        return stationNameCache.computeIfAbsent(stationId, id -> {
            BusStation bs = busStationMapper.findByStationId(id);
            return bs != null ? bs.getStationNm() : id;
        });
    }

    // Helper: routeId에 해당하는 노선의 이름 조회
    private String getRouteName(String routeId) {
        BusRoute br = getRouteInfo(routeId);
        return br != null ? br.getRouteNm() : routeId;
    }

    // Helper: 주어진 stop list에서 stationId의 인덱스 반환 (없으면 -1)
    private int indexOf(List<BusStationRoute> stops, String stationId) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getStationId().equals(stationId)) {
                return i;
            }
        }
        return -1;
    }
    
    // 경로 탐색을 위한 내부 상태 클래스 (경량화 버전)
    private static class PathState {
        String stationId;              // 현재 정류장 ID
        String routeId;                // 현재 노선 ID
        String updnDir;                // 현재 상/하행 방향
        int transferCount = 0;         // 환승 횟수
        PathState prevState = null;    // 이전 상태에 대한 참조
        boolean isTransfer = false;    // 환승 여부
    }
}
