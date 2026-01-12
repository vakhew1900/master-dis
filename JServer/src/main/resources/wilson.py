from math import sqrt
from scipy.stats import norm


from math import sqrt
from scipy.stats import norm


def wilson_score_interval(p_hat: float, confidence_level: float, n: int):
    alpha = 1.0 - confidence_level
    z = norm.ppf(1.0 - alpha / 2.0)
    z2 = z * z

    denominator = 1.0 + z2 / n
    center = p_hat + z2 / (2.0 * n)
    margin = z * sqrt((p_hat * (1.0 - p_hat) + z2 / (4.0 * n)) / n)

    lower = (center - margin) / denominator
    upper = (center + margin) / denominator

    return lower, upper




methods = {
    "Bruteforce": 0.9375,
    "MCS tree search": 0.8281,
    "Branch matching": 0.9844,
    "Backtracking": 0.5625
}

for name, p_hat in methods.items():
    ci = wilson_score_interval(p_hat, 0.95, 64)
    print(f"{name}: CI = [{ci[0]:.4f}, {ci[1]:.4f}]")