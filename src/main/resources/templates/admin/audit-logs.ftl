<#import "../layout.ftl" as layout>
<@layout.page title="Лог дій">
    <div class="header-section">
        <h1>Лог дій адміністраторів</h1>
        <p class="subtitle">Історія змін статусів заявок</p>
    </div>

    <div class="content-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Час</th>
                    <th>Користувач</th>
                    <th>Дія</th>
                    <th>Об'єкт</th>
                </tr>
            </thead>
            <tbody>
                <#list logs as log>
                    <tr>
                        <td>${log.formattedTimestamp}</td>
                        <td>${log.userId}</td>
                        <td>${log.action}</td>
                        <td>${log.target}</td>
                    </tr>
                <#else>
                    <tr>
                        <td colspan="4" class="empty">Логів поки немає.</td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>
    
    <style>
        .header-section { margin-bottom: 30px; }
        .subtitle { color: #64748b; margin: 4px 0 0 0; }
        .content-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .data-table { width: 100%; border-collapse: collapse; }
        .data-table th { text-align: left; padding: 12px; border-bottom: 2px solid #f1f5f9; color: #64748b; font-size: 13px; text-transform: uppercase; letter-spacing: 0.025em; }
        .data-table td { padding: 16px 12px; border-bottom: 1px solid #f1f5f9; }
        .empty { text-align: center; color: #94a3b8; padding: 40px; }
    </style>
</@layout.page>
