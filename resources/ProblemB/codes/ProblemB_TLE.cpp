#include <iostream>
#include <vector>
#include <queue>

using namespace std;

int main() {
    int n;
    if (!(cin >> n)) return 0;
    vector<vector<pair<int, int>>> adj(n + 1);
    for (int i = 0; i < n - 1; ++i) {
        int u, v, w;
        cin >> u >> v >> w;
        adj[u].push_back({v, w});
        adj[v].push_back({u, w});
    }
    
    vector<int> dist(n + 1, 0);
    vector<bool> visited(n + 1, false);
    queue<int> q;
    q.push(1);
    visited[1] = true;
    
    while (!q.empty()) {
        int u = q.front();
        q.pop();
        for (auto& edge : adj[u]) {
            if (!visited[edge.first]) {
                visited[edge.first] = true;
                dist[edge.first] = dist[u] ^ edge.second;
                q.push(edge.first);
            }
        }
    }
    
    long long total_luck = 0;
    for (int i = 1; i <= n; ++i) {
        for (int j = i + 1; j <= n; ++j) {
            total_luck += (dist[i] ^ dist[j]);
        }
    }
    
    cout << total_luck << endl;
    return 0;
}