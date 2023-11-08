# Systems Modeling

## Lab 1

### Random number generators and verifying the correspondence to PDF

3 different distributions available:
- Exponential
- Normal
- Uniform

Histogram plot of each dist.
Calculate mean and variance.
Measured Xi<sup>2</sup> to see the accuracy.

## Lab 2

### Imitation model of discreet event system

Implemeted 3 different models:
- Simple: 1 Creator, 1 Processor, 1 Destroyer.
- Chained: 1 Creator, 3 Processors, 1 Destroyer (All items linked in chain).
- Complex: 1 Creator, 5 Processors, 1 Destroyer. All items are linked as follows:
    ```
         Process - Process
    0.3 /                  \
  Create                     Process - Despose
    0.7 \                  /
          Process - Process
  ```
  
## Lab 3

### Queueing model: Banking problem and Hospital problem

Implement 2 different models:
- Banking: 1 Creator, 2 Processors, 1 Destroyer (Initial state present, Redistribution of events enabled).
- Hospital: 1 Creator, 3 Multichannel processes, 2 Ordinary processes, 2 Destroyers (Predicate forwarding is enabled).
