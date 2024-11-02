<template>
    <div class="notification-container">
        <div class="bell" id="notification-bell" @click="toggleNotificationDropdown">
      <span class="notification-count" id="notification-count" v-if="unreadCount > 0">
        {{ unreadCount }}
      </span>
            🔔
        </div>
        <div class="dropdown" id="notification-dropdown" v-if="showNotificationDropdown">
            <ul id="notification-list">
                <li
                        v-for="notification in notifications"
                        :key="notification.id"
                        :data-id="notification.id"
                        :style="{ textDecoration: notification.read ? 'line-through' : 'none' }"
                        @click="markAsRead(notification.id)"
                >
                    <strong>{{ notification.title }}</strong>
                    <br />
                    {{ notification.message }}
                </li>
            </ul>
        </div>
        <input type="hidden" id="currentUserId" :value="userId" />
        <input type="hidden" name="_csrf" :value="csrfToken" />
    </div>
</template>

<script>
    import { ref, onMounted, onUnmounted } from 'vue';
    import SockJS from 'sockjs-client';
    import Stomp from 'stompjs';

    export default {
        setup() {
            const userId = ref(document.getElementById('currentUserId').value);
            const csrfToken = ref(document.querySelector('input[name="_csrf"]').value);
            const notifications = ref([]);
            const unreadCount = ref(0);
            const showNotificationDropdown = ref(false);

            const toggleNotificationDropdown = () => {
                showNotificationDropdown.value = !showNotificationDropdown.value;
            };

            const markAsRead = (id) => {
                fetch(`/api/notifications/read/${id}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken.value,
                    },
                })
                    .then((response) => {
                        if (response.ok) {
                            notifications.value = notifications.value.map((n) =>
                                n.id === id ? { ...n, read: true } : n
                            );
                            unreadCount.value--;
                        } else {
                            console.error('Ошибка при отметке уведомления как прочитанное');
                        }
                    })
                    .catch((error) => {
                        console.error('Ошибка при отметке уведомления как прочитанное:', error);
                    });
            };

            onMounted(() => {
                const socket = new SockJS('/ws');
                const stompClient = Stomp.over(socket);

                stompClient.connect({}, () => {
                    console.log('Connected to WebSocket');
                    stompClient.subscribe(`/user/${userId.value}/topic/notifications`, (notification) => {
                        const data = JSON.parse(notification.body);
                        notifications.value.push(data);
                        unreadCount.value++;
                    });
                });

                fetch(`/api/notifications/unread/${userId.value}`)
                    .then((response) => response.json())
                    .then((data) => {
                        notifications.value = data;
                        unreadCount.value = data.length;
                    })
                    .catch((error) => {
                        console.error('Ошибка при загрузке непрочитанных уведомлений:', error);
                    });

                onUnmounted(() => {
                    stompClient.disconnect();
                });
            });

            return {
                userId,
                csrfToken,
                notifications,
                unreadCount,
                showNotificationDropdown,
                toggleNotificationDropdown,
                markAsRead,
            };
        },
    };
</script>
