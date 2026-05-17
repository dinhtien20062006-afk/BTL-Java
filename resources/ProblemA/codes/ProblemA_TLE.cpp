#include <iostream>
#include <vector>

using namespace std;

const int MOD = 1e9 + 7;

long long solve(int day, vector<int> cur_a, int n, int k) {
    if (day == k) {
        for (int i = 0; i < n - 1; i++) {
            if (cur_a[i] >= cur_a[i + 1]) return 0;
        }
        return 1;
    }
    long long ans = 0;
    for (int l = 0; l < n; l++) {
        for (int r = l; r < n; r++) {
            vector<int> next_a = cur_a;
            for (int j = l; j <= r; j++) next_a[j]++;
            ans = (ans + solve(day + 1, next_a, n, k)) % MOD;
        }
    }
    return ans;
}

int main() {
    int n, k;
    if (!(cin >> n >> k)) return 0;
    vector<int> a(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    cout << solve(0, a, n, k) << endl;
    return 0;
}