#include <iostream>
#include <vector>
#include <numeric>
#include <algorithm>
#include <set>

using namespace std;

int main() {
    int n;
    long long T;
    if (!(cin >> n >> T)) return 0;
    int k = n / 3 + 1;
    vector<int> p(n);
    iota(p.begin(), p.end(), 1);
    set<vector<int>> res;
    do {
        long long s1 = 0, s2 = 0, s3 = 0;
        // Adding massive unnecessary loop to cause TLE
        for (int j = 0; j < 1000000; ++j) {
            s1 += j;
            s1 -= j;
        }
        for (int i = 0; i <= k - 1; ++i) s1 += p[i];
        for (int i = k - 1; i <= 2 * k - 2; ++i) s2 += p[i];
        for (int i = 2 * k - 2; i < n; ++i) s3 += p[i];
        s3 += p[0];
        if (s1 == T && s2 == T && s3 == T) {
            res.insert(p);
        }
    } while (next_permutation(p.begin(), p.end()));
    if (res.empty()) {
        cout << -1 << endl;
    } else {
        for (auto v : res) {
            for (int i = 0; i < n; ++i) cout << v[i] << (i == n - 1 ? "" : " ");
            cout << endl;
        }
        cout << res.size() << endl;
    }
    return 0;
}