(() => {
  const toastContainer = document.getElementById('toast-container');
  const validLevels = new Set(['info', 'warn', 'error', 'success']);

  window.showToast = function showToast(message, level = 'info', visibleTime = 4000) {
    if (!toastContainer) return null;

    const requestedLevel = String(level).toLowerCase();
    const normalizedLevel = requestedLevel === 'warning' ? 'warn' : requestedLevel;
    const toastLevel = validLevels.has(normalizedLevel) ? normalizedLevel : 'info';
    const duration = Number.isFinite(Number(visibleTime))
      ? Math.max(0, Number(visibleTime))
      : 4000;

    const toast = document.createElement('div');
    toast.className = `toast toast-${toastLevel}`;
    toast.setAttribute('role', toastLevel === 'error' ? 'alert' : 'status');

    const icon = document.createElement('span');
    icon.className = `toast-icon toast-icon-${toastLevel}`;
    icon.setAttribute('aria-hidden', 'true');
    icon.textContent = toastLevel === 'info' ? 'i' : toastLevel === 'success' ? '+' : '!';

    const text = document.createElement('span');
    text.textContent = String(message ?? '');

    toast.append(icon, text);
    toastContainer.appendChild(toast);

    window.setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      toast.style.transition = 'opacity 300ms ease, transform 300ms ease';
      window.setTimeout(() => toast.remove(), 300);
    }, duration);

    return toast;
  };
})();