document.addEventListener('keydown', function(e) {

    if ((e.ctrlKey || e.metaKey) && e.key === 'f') { // Hỗ trợ cả Cmd+F trên Mac
        e.preventDefault();
        // Thử tìm các ID phổ biến của ô search
        const searchInput = document.querySelector('input[name="search"]') || document.getElementById('filterSearch');
        if (searchInput) {
            searchInput.focus();
        }
    }
});

function showNotification(message, type = 'info') {
    const container = document.getElementById('notificationContainer');
    if (!container) {
        console.error('Notification container not found!');
        return;
    }

    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;

    let iconClass = 'fa-info-circle'; // Mặc định
    if (type === 'success') iconClass = 'fa-check-circle';
    else if (type === 'error') iconClass = 'fa-exclamation-triangle';
    else if (type === 'warning') iconClass = 'fa-exclamation-circle';


    notification.innerHTML = `
        <span class="notification-icon fas ${iconClass}"></span>
        <span class="notification-message">${message}</span>
        <button class="notification-close" onclick="this.parentElement.remove()">&times;</button>
    `;

    container.appendChild(notification);

    // Auto remove after 5 seconds
    setTimeout(() => {
        // Dùng hiệu ứng fade out trước khi xóa
        $(notification).fadeOut(function() { $(this).remove(); });
    }, 5000);
}

window.showAdminNotification = showNotification;

