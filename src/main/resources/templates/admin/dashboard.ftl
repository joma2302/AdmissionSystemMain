<#import "../layout.ftl" as layout>
<@layout.page title="Адмін-панель">
    <div class="header-section">
        <div>
            <h1>Панель адміністратора</h1>
            <p class="subtitle">Огляд поточної вступної кампанії та швидкі дії</p>
        </div>
        <div class="quick-actions">
            <a href="/admin/applications" class="action-btn">Керувати заявками</a>
            <a href="/admin/faculties" class="action-btn">Факультети та місця</a>
            <a href="/admin/applicants" class="action-btn">Абітурієнти</a>
            <a href="/admin/audit-logs" class="action-btn">Лог дій</a>
            <a href="/admission" class="action-btn primary">Провести зарахування</a>
        </div>
    </div>

    <div class="metric-grid">
        <div class="metric-card">
            <div class="metric-icon">👥</div>
            <div class="metric-content">
                <span>Абітурієнтів</span>
                <strong>${stats.applicants()}</strong>
            </div>
        </div>
        <div class="metric-card">
            <div class="metric-icon">📝</div>
            <div class="metric-content">
                <span>Заявок</span>
                <strong>${stats.applications()}</strong>
            </div>
        </div>
        <div class="metric-card warning">
            <div class="metric-icon">⏳</div>
            <div class="metric-content">
                <span>Очікують рішення</span>
                <strong>${stats.pending()}</strong>
            </div>
        </div>
        <div class="metric-card success">
            <div class="metric-icon">✅</div>
            <div class="metric-content">
                <span>Зараховано</span>
                <strong>${stats.admitted()}</strong>
            </div>
        </div>
        <div class="metric-card danger">
            <div class="metric-icon">❌</div>
            <div class="metric-content">
                <span>Відхилено</span>
                <strong>${stats.rejected()}</strong>
            </div>
        </div>
        <div class="metric-card info">
            <div class="metric-icon">🚪</div>
            <div class="metric-content">
                <span>Вільних місць</span>
                <strong>${stats.freeSeats()} / ${stats.seats()}</strong>
            </div>
        </div>
    </div>

    <div class="dashboard-content">
        <div class="content-card" style="margin-bottom: 20px;">
            <h2>Статистика наповненості факультетів</h2>
            <div style="width: 100%; height: 300px;">
                <canvas id="facultyChart"></canvas>
            </div>
        </div>
        <div class="content-card">
            <h2>Попит за спеціальностями</h2>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Факультет</th>
                        <th>Місць</th>
                        <th>Заявок</th>
                        <th>Конкурс</th>
                        <th>Стан набору</th>
                    </tr>
                </thead>
                <tbody>
                    <#list facultyDemand as item>
                        <#assign occupancy = 0>
                        <#if item.maxStudents() gt 0>
                            <#assign occupancy = (item.admittedCount() / item.maxStudents() * 100)>
                        </#if>
                        <tr>
                            <td><strong>${item.name()}</strong></td>
                            <td>${item.maxStudents()}</td>
                            <td>${item.applicationCount()}</td>
                            <td><span class="badge competition">${item.competition()?string["0.00"]}</span></td>
                            <td>
                                <div class="progress-container">
                                    <div class="progress-bar" style="width: ${occupancy}%"></div>
                                    <span class="progress-text">${item.admittedCount()} / ${item.maxStudents()}</span>
                                </div>
                            </td>
                        </tr>
                    <#else>
                        <tr>
                            <td colspan="5" class="empty">Спеціальності ще не додані.</td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        const ctx = document.getElementById('facultyChart').getContext('2d');
        const facultyChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: [<#list facultyDemand as item>'${item.name()?js_string}'<#if item?has_next>, </#if></#list>],
                datasets: [{
                    label: 'Кількість зарахованих',
                    data: [<#list facultyDemand as item>${item.admittedCount()}<#if item?has_next>, </#if></#list>],
                    backgroundColor: 'rgba(16, 185, 129, 0.6)',
                    borderColor: 'rgba(16, 185, 129, 1)',
                    borderWidth: 1
                }, {
                    label: 'Максимум місць',
                    data: [<#list facultyDemand as item>${item.maxStudents()}<#if item?has_next>, </#if></#list>],
                    backgroundColor: 'rgba(59, 130, 246, 0.6)',
                    borderColor: 'rgba(59, 130, 246, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    </script>

    <style>
        .header-section { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 30px; }
        .subtitle { color: #64748b; margin: 4px 0 0 0; }
        
        .action-btn { display: inline-block; background: #fff; color: #1e293b; padding: 10px 18px; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 14px; border: 1px solid #e2e8f0; transition: all 0.2s; box-shadow: 0 1px 2px rgba(0,0,0,0.05); }
        .action-btn:hover { background: #f8fafc; border-color: #cbd5e1; }
        .action-btn.primary { background: #2563eb; color: #fff; border-color: #2563eb; }
        .action-btn.primary:hover { background: #1d4ed8; }
        
        .metric-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .metric-card { background: #fff; border-radius: 12px; padding: 20px; display: flex; align-items: center; gap: 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); border-top: 4px solid #3b82f6; }
        .metric-icon { font-size: 24px; background: #f1f5f9; width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; border-radius: 10px; }
        .metric-content span { display: block; color: #64748b; font-size: 14px; font-weight: 500; }
        .metric-content strong { font-size: 24px; color: #1e293b; }
        
        .metric-card.warning { border-top-color: #f59e0b; }
        .metric-card.success { border-top-color: #10b981; }
        .metric-card.danger { border-top-color: #ef4444; }
        .metric-card.info { border-top-color: #06b6d4; }

        .content-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th { text-align: left; padding: 12px; border-bottom: 2px solid #f1f5f9; color: #64748b; font-size: 13px; text-transform: uppercase; letter-spacing: 0.025em; }
        .data-table td { padding: 16px 12px; border-bottom: 1px solid #f1f5f9; }
        
        .badge { padding: 4px 8px; border-radius: 6px; font-weight: 600; font-size: 12px; }
        .badge.competition { background: #eff6ff; color: #2563eb; }
        
        .progress-container { background: #f1f5f9; height: 20px; border-radius: 10px; position: relative; overflow: hidden; min-width: 120px; }
        .progress-bar { background: #10b981; height: 100%; transition: width 0.5s; }
        .progress-text { position: absolute; top: 0; left: 0; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 700; color: #1e293b; }
        
        .empty { text-align: center; color: #94a3b8; padding: 40px; }
    </style>
</@layout.page>
