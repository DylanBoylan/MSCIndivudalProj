--- 'SecurePass123!' is unhashed password always ---

--- Test Admin ---
INSERT INTO users (email, password, role) VALUES ('test_administrator@example.com', '$2a$10$HZRxlurLYB4WOziqN57xY.KBu3l0sNpRTn/tR27H6RBKLW0zSAF6W', 'ADMINISTRATOR');

--- Test Customer Service ---
INSERT INTO users (email, password, role) VALUES ('test_customer_service@example.com', '$2a$10$HZRxlurLYB4WOziqN57xY.KBu3l0sNpRTn/tR27H6RBKLW0zSAF6W', 'CUSTOMER_SERVICE');

--- Test Network Management Engineer ---
INSERT INTO users (email, password, role) VALUES ('test_network_management_engineer@example.com', '$2a$10$HZRxlurLYB4WOziqN57xY.KBu3l0sNpRTn/tR27H6RBKLW0zSAF6W', 'NETWORK_MANAGEMENT_ENGINEER');

--- Test Support Engineer ---
INSERT INTO users (email, password, role) VALUES ('test_support_engineer@example.com', '$2a$10$HZRxlurLYB4WOziqN57xY.KBu3l0sNpRTn/tR27H6RBKLW0zSAF6W', 'SUPPORT_ENGINEER');

