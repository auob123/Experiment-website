---
config:
  layout: dagre
---
flowchart LR

    %% Main Entry
    Start(["Старт"]) --> Auth{"Вход или Регистрация?"}
    Auth -- Вход --> Login["Ввести данные для входа и выбрать роль"]
    Auth -- Регистрация --> Register["Заполнить форму регистрации"]
    Register --> RegValid{"Данные корректны?"}
    RegValid -- Да --> CreateUser["Создать пользователя"]
    RegValid -- Нет --> RegError["Показать ошибку регистрации"]
    CreateUser --> Login
    Login --> LoginValid{"Данные и роль верны?"}
    LoginValid -- Да --> RoleRouter["Переход к роли"]
    LoginValid -- Нет --> LoginError["Показать ошибку входа"]

    %% Student Flow (full)
    RoleRouter -- Студент --> Dashboard["Главная страница (студент)"]
    Dashboard --> ChooseAction{"Выберите действие"}
    ChooseAction -- Просмотр экспериментов --> BrowseExp["Список экспериментов"]
    ChooseAction -- Уведомления --> ViewNotif["Просмотр уведомлений"]
    ChooseAction -- Выйти --> Logout["Выход"]
    ChooseAction -- Профиль --> Profile["Профиль"]

    BrowseExp --> SelectExp{"Выбрать эксперимент?"}
    SelectExp -- Да --> ViewExp["Просмотр деталей эксперимента"]
    SelectExp -- Нет --> ChooseAction
    %% Добавлено: Проверка разрешения от родителя
    ViewExp -. Разрешение родителя? .-> ParentAllow["Родитель разрешил просмотр?"]
    ParentAllow -- Да --> ViewExpContinue["Детали эксперимента доступны"]
    ParentAllow -- Нет --> NoAccess["Доступ запрещен"]
    ViewExpContinue --> CommentPrompt{"Добавить комментарий?"}
    ViewExpContinue --> StartExp{"Начать эксперимент?"}
    NoAccess --> ChooseAction

    %% Эксперимент (без изменений)
    subgraph ExpFlow [Эксперимент]
        direction LR
        StartExp -- Да --> PerformSteps["Выполнение шагов эксперимента"]
        StartExp -- Нет --> ChooseAction
        PerformSteps --> StepDecision{"Все шаги выполнены?"}
        StepDecision -- Нет --> NextStep["Следующий шаг"]
        NextStep --> PerformSteps
        StepDecision -- Да --> SubmitData["Отправить данные эксперимента"]
        SubmitData --> FeedbackPrompt{"Оставить отзыв?"}
        FeedbackPrompt -- Да --> GiveFeedback["Заполнить форму отзыва"]
        FeedbackPrompt -- Нет --> ChooseAction
        GiveFeedback --> ChooseAction
    end

    CommentPrompt -- Да --> AddComment["Написать комментарий"]
    AddComment --> ChooseAction
    CommentPrompt -- Нет --> ChooseAction

    CreateExp --> ExpValid{"Эксперимент корректен?"}
    ExpValid -- Да --> SaveExp["Сохранить эксперимент"]
    ExpValid -- Нет --> ExpError["Показать ошибку"]
    SaveExp --> ChooseAction
    ExpError --> CreateExp

    ViewNotif --> ChooseAction
    Profile --> ChooseAction
    Logout --> End(["Конец"])

    %% Teacher Flow (full)
    RoleRouter -- Преподаватель --> T_Dashboard["Главная страница (преподаватель)"]
    T_Dashboard --> T_ChooseAction{"Выберите действие"}
    T_ChooseAction -- Создать инструкцию --> T_CreateInstr["Создать инструкцию"]
    T_ChooseAction -- Просмотр экспериментов --> T_BrowseExp["Список экспериментов"]
    T_ChooseAction -- Выйти --> T_Logout["Выход"]

    T_CreateInstr --> T_EditInstr["Редактировать инструкцию"]
    T_EditInstr --> T_SubmitInstr["Отправить инструкцию на проверку"]
    T_SubmitInstr --> T_AIValidate{"AI валидация инструкции?"}
    T_AIValidate -- Да --> T_InstrValid["Инструкция одобрена"]
    T_AIValidate -- Нет --> T_InstrError["Показать замечания AI"]
    T_InstrError --> T_EditInstr
    T_InstrValid --> T_ChooseAction

    T_BrowseExp --> T_ChooseAction
    T_Logout --> End

    %% Parent, Admin, Mediator (grouped, minimal)
    RoleRouter -- Родитель --> P_Dashboard["Главная страница (родитель)"]
    P_Dashboard --> P_ViewNotif["Просмотр уведомлений"]
    P_Dashboard --> P_ViewChildProgress["Достижения ребенка"]
    P_Dashboard --> P_AllowExp["Разрешить просмотр эксперимента"]
    P_Dashboard --> P_Logout["Выход"]
    P_ViewNotif --> P_Dashboard
    P_ViewChildProgress --> P_Dashboard
    P_AllowExp --> P_Dashboard
    P_AllowExp -- Разрешено --> ParentAllow
    P_Logout --> End

    RoleRouter -- Администратор --> A_Dashboard["Панель администратора"]
    A_Dashboard --> A_ManageUsers["Управление пользователями"]
    A_Dashboard --> A_ManageContent["Управление контентом"]
    A_Dashboard --> A_Logout["Выход"]
    A_ManageUsers --> A_Dashboard
    A_ManageContent --> A_Dashboard
    A_Logout --> End

    RoleRouter -- Медиатор --> M_Dashboard["Главная страница (медиатор)"]
    M_Dashboard --> M_Resolve["Рассмотрение споров"]
    M_Dashboard --> M_Logout["Выход"]
    M_Resolve --> M_Dashboard
    M_Logout --> End
