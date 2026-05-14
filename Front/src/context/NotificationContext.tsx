import React, { createContext, useContext, useState, ReactNode } from 'react';

interface Notification {
  id: number;
  message: string;
}

interface NotificationContextType {
  notifications: Notification[];
  addError: (message: string) => void;
  removeNotification: (id: number) => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const NotificationProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  React.useEffect(() => {
    const handleError = (e: any) => addError(e.detail);
    window.addEventListener('global-error', handleError);
    return () => window.removeEventListener('global-error', handleError);
  }, []);

  const addError = (message: string) => {
    const id = Date.now();
    setNotifications((prev) => [...prev, { id, message }]);
  };

  const removeNotification = (id: number) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  return (
    <NotificationContext.Provider value={{ notifications, addError, removeNotification }}>
      {children}
      <div className="error-container">
        {notifications.map((n) => (
          <div 
            key={n.id} 
            onClick={() => removeNotification(n.id)}
            className="error-toast"
          >
            Ошибка: {n.message} (нажми, чтобы скрыть)
          </div>
        ))}
      </div>
    </NotificationContext.Provider>
  );
};

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) throw new Error('useNotification must be used within a NotificationProvider');
  return context;
};
