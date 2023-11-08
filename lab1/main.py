import numpy as np
from scipy import special
from matplotlib import pyplot as plt
from util import normal_rand, exp_rand


if __name__ == '__main__':
    ##### Exponential
    # n = 10000
    # for l in np.arange(0.1, 1.1, 0.1):
    #     dst = [exp_rand(l) for i in range(n)]
    #     res = plt.hist(dst, bins=30)
    #     data, edges, _ = res
    #     F = 1 - np.exp(- l * edges)
    #     estimate = n * (F[1:] - F[:-1])
    #     xi = np.sum(((data - estimate) ** 2) / estimate)
    #     mean, variance = np.mean(dst), np.var(dst)
    #     print('Lambda -', l)
    #     print('Mean -', mean, ', Expected:', 1 / l)
    #     print('Variance -', variance, ', Expected:', 1 / l ** 2)
    #     print('Xi -', xi)
    #     print()
    #     plt.show()

    ###### Gaussian
    n = 10000
    std = 1
    m = 0
    dst = [normal_rand(std, m) for i in range(n)]
    res = plt.hist(dst, bins=30)
    data, edges, _ = res
    v = (edges - m) / (std * np.sqrt(2))
    F = 0.5 * (1 + special.erf(v))
    estimate = n * (F[1:] - F[:-1])
    xi = np.sum(((data - estimate) ** 2) / estimate)
    mean, variance = np.mean(dst), np.var(dst)
    print('Mean -', mean, ', Expected:', m)
    print('Variance -', variance, ', Expected:', std**2)
    print('Xi -', xi)
    print()
    plt.show()

    ###### Uniform
    # n = 10000
    # z = 100
    # a = 5 ** 13
    # c = 2 ** 31
    # values = []
    # for i in range(n):
    #     z = (a * z) % c
    #     x = z / c
    #     values.append(x)
    # values = np.array(values)
    # res = plt.hist(values, bins=30)
    # data, edges, _ = res
    # estimate = n * (edges[1:] - edges[:-1])
    # xi = np.sum(((data - estimate) ** 2) / estimate)
    # mean, variance = np.mean(values), np.var(values)
    # print('Mean -', mean, ', Expected:', 0.5)
    # print('Variance -', variance, ', Expected:', 1/12)
    # print('Xi -', xi)
    # print()
    # plt.show()
