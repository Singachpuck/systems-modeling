from random import uniform
from math import log


def exp_rand(l):
    c = uniform(0, 1)
    return - (log(c) / l)


def normal_rand(std, mean):
    m = sum((uniform(0, 1) for i in range(12))) - 6
    return std * m + mean
