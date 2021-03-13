import numpy as np

def normalize(X):
    mu = X.mean(0)
    Xbar = X - mu
    return Xbar, mu

def eig(S):
    eigvals, eigvecs = np.linalg.eig(S)
    sort_indices = np.argsort(eigvals)[::-1]
    return eigvals[sort_indices], eigvecs[:, sort_indices]

def projection_matrix(B):
    return B @ np.linalg.inv(B.T @ B) @ B.T

def PCA_high_dim(X, num_components):
    N, D = X.shape
    X_normalized, mean = normalize(X)
    S = np.dot(X_normalized, X_normalized.T) / N
    eig_vals, eig_vecs = eig(S)

    principal_values = eig_vals[:num_components]
    principal_components = eig_vecs[:, :num_components]
    
    P = projection_matrix(principal_components)
    reconst = (P @ X_normalized) + mean
    return reconst, mean, principal_values, principal_components