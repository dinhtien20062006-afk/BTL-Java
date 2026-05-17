#include <iostream>
#include <vector>
#include <cstring>

using namespace std;

const int MOD = 1e9 + 7;
long long nCr[25][25];
long long dp[21][21][21][21];

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);

    int n, k;
    if (!(cin >> n >> k)) return 0;
    vector<int> a(n + 1);
    for (int i = 1; i <= n; ++i) cin >> a[i];

    for (int i = 0; i <= 20; ++i) {
        nCr[i][0] = 1;
        for (int j = 1; j <= i; ++j)
            nCr[i][j] = (nCr[i - 1][j - 1] + nCr[i - 1][j]) % MOD;
    }

    memset(dp, 0, sizeof(dp));

    for (int B1 = 0; B1 <= k; ++B1) {
        int S1 = B1;
        int K1 = B1;
        if (n == 1 && K1 != k) continue;
        for (int active1 = 0; active1 <= S1; ++active1) {
            if (n == 1 && active1 != 0) continue;
            int E1 = S1 - active1;
            long long ways = (nCr[k][B1] * nCr[S1][E1]) % MOD;
            dp[1][S1][active1][K1] = (dp[1][S1][active1][K1] + ways) % MOD;
        }
    }

    for (int i = 1; i < n; ++i) {
        for (int Si = 0; Si <= k; ++Si) {
            for (int activei = 0; activei <= Si; ++activei) {
                for (int Ki = 0; Ki <= k; ++Ki) {
                    if (dp[i][Si][activei][Ki] == 0) continue;
                    for (int Snext = 0; Snext <= k; ++Snext) {
                        if (a[i + 1] + Snext <= a[i] + Si) continue;
                        int Bnext = Snext - activei;
                        if (Bnext < 0 || Ki + Bnext > k) continue;
                        int Knext = Ki + Bnext;
                        if (i + 1 == n && Knext != k) continue;
                        for (int activenext = 0; activenext <= Snext; ++activenext) {
                            if (i + 1 == n && activenext != 0) continue;
                            int Enext = Snext - activenext;
                            long long ways = (nCr[k - Ki][Bnext] * nCr[Snext][Enext]) % MOD;
                            long long term = (dp[i][Si][activei][Ki] * ways) % MOD;
                            dp[i + 1][Snext][activenext][Knext] = (dp[i + 1][Snext][activenext][Knext] + term) % MOD;
                        }
                    }
                }
            }
        }
    }

    long long ans = 0;
    if (n == 1) {
        for (int S1 = 0; S1 <= k; ++S1) ans = (ans + dp[1][S1][0][k]) % MOD;
    } else {
        for (int Sn = 0; Sn <= k; ++Sn) {
            ans = (ans + dp[n][Sn][0][k]) % MOD;
        }
    }
    cout << ans << endl;

    return 0;
}