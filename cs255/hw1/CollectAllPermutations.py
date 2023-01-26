from math import *
from random import *

def permutation(n, mode):
	iterations = 0
	completed = 0
	n_factorial = factorial(n)
	seen = []
	A = []
	for i in range(1,n+1):
		A.append(i)

	while completed < n_factorial:
		arr = randomly_permutate_in_place(A)
		if mode == 'verbose':
			print(*arr)
			print(seen)
		if arr not in seen: #TODO
			seen.append(arr)
			completed += 1
		iterations += 1

	print("All permutations of " + str(n) + " collected in " + str(iterations) + " iterations")

def randomly_permutate(A):
	n = len(A)
	P = [0] * n
	P_map = {}

	rand = randint(1, n**3)
	P[0] = rand
	P_map[P[0]] = A[0]

	for i in range(1,n):
		# this is to prevent duplicate priority keys
		while rand in P:
			rand = randint(1, n**3)
		P[i] = rand
		P_map[P[i]] = A[i]

	P = mergesort(P)
	newA = P
	for i in range(0, n):
		newA[i] = P_map[P[i]]
	return newA

def randomly_permutate_in_place(A):
	newA = A
	n = len(A)
	for i in range(0,n):
		rand = randint(i, n-1)
		newA[i], newA[rand] = newA[rand], newA[i]
	return newA

def mergesort(A):
	n = len(A)
	if n==1:
		return A

	middle_index = len(A)//2
	left = A[:middle_index]
	right = A[middle_index:]
	left = mergesort(left)
	right = mergesort(right)

	return merge(left, right, n)

def merge(L, R, size):
	newlist = [0] * size
	i = 0
	j = 0
	k = 0

	while i < len(L) and j < len(R) and k < size:
		if L[i] > R[j]:
			newlist[k] = R[j]
			j+=1
			k+=1
		elif L[i] <= R[j]:
			newlist[k] = L[i]
			i+=1
			k+=1
	while i < len(L) and k < size:
		newlist[k] = L[i]
		i+=1
		k+=1
	while j < len(R) and k < size:
		newlist[k] = R[j]
		j+=1
		k+=1
	return newlist

if __name__ == '__main__':
	n, mode = input("Enter the size and mode: ").split()
	n = int(n)
	permutation(n, mode)
