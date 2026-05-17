#include <iostream>
#include <vector>
#include <queue>

using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(NULL);
    
    int n;
    if (!(cin >> n)) return 0;
    
    vector<vector<pair<int, int>>> adj(n + 1);
    for (int i = 0; i < n - 1; ++i) {
        int u, v, w;
        if (!(cin >> u >> v >> w)) break;
        adj[u].push_back({v, w});
        adj[v].push_back({u, w});
    }
    
    vector<int> dist(n + 1, 0);
    vector<bool> visited(n + 1, false);
    queue<int> q;
    
    q.push(1);
    visited[1] = true;
    dist[1] = 0;
    
    while (!q.empty()) {
        int u = q.front();
        q.pop();
        for (auto& edge : adj[u]) {
            int v = edge.first;
            int w = edge.second;
            if (!visited[v]) {
                visited[v] = true;
                dist[v] = dist[u] ^ w;
                q.push(v);
            }
        }
    }
    
    long long total_luck = 0;
    for (int k = 0; k < 27; ++k) {
        long long count1 = 0;
        for (int i = 1; i <= n; ++i) {
            if ((dist[i] >> k) & 1) {
                count1++;
            }
        }
        long long count0 = n - count1;
        total_luck += (count1 * count0) * (1LL << k);
    }
    
    cout << total_luck << endl;
    
    return 0;
}