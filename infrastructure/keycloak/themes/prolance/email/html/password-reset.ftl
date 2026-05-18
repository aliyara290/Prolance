<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif;
            background-color: #f0f2f5;
            color: #121320;
            padding: 40px 16px;
        }
        .wrapper {
            max-width: 560px;
            margin: 0 auto;
        }
        .logo-row {
            text-align: center;
            margin-bottom: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }
        .logo-icon {
            width: 40px;
            height: 40px;
            background-color: #267af7;
            border-radius: 8px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }
        .logo-name {
            font-size: 20px;
            font-weight: 700;
            color: #121320;
            letter-spacing: -0.02em;
        }
        .card {
            background-color: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 40px 40px 32px;
            box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
        }
        .card-icon {
            width: 52px;
            height: 52px;
            background-color: #e8f1ff;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 20px;
        }
        .card-title {
            font-size: 20px;
            font-weight: 700;
            color: #121320;
            margin-bottom: 8px;
        }
        .card-body {
            font-size: 15px;
            color: #555555;
            line-height: 1.65;
        }
        .card-body p {
            margin-bottom: 12px;
        }
        .divider {
            border: none;
            border-top: 1px solid #e2e8f0;
            margin: 28px 0;
        }
        .btn-container {
            text-align: center;
            margin: 28px 0;
        }
        .btn {
            display: inline-block;
            background-color: #267af7;
            color: #ffffff !important;
            text-decoration: none;
            font-weight: 600;
            font-size: 15px;
            padding: 13px 32px;
            border-radius: 8px;
            letter-spacing: 0.01em;
        }
        .link-fallback {
            background-color: #f1f5f9;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            padding: 10px 14px;
            margin-top: 20px;
            word-break: break-all;
        }
        .link-fallback p {
            font-size: 12px;
            color: #64748b;
            margin-bottom: 6px;
        }
        .link-fallback a {
            font-size: 12px;
            color: #267af7;
            word-break: break-all;
        }
        .warning {
            background-color: #fff8ec;
            border: 1px solid #f59e0b;
            border-radius: 6px;
            padding: 10px 14px;
            font-size: 13px;
            color: #92400e;
            margin-top: 16px;
            line-height: 1.5;
        }
        .footer {
            margin-top: 24px;
            text-align: center;
            font-size: 12px;
            color: #94a3b8;
            line-height: 1.6;
        }
        .footer a {
            color: #64748b;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="wrapper">

        <!-- Logo -->
        <div class="logo-row">
            <div class="logo-icon">
                <svg width="24" height="24" fill="none" viewBox="0 0 24 24" stroke="white" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
            </div>
            <span class="logo-name">Prolance</span>
        </div>

        <!-- Card -->
        <div class="card">

            <div class="card-icon">
                <svg width="28" height="28" fill="none" viewBox="0 0 24 24" stroke="#267af7" stroke-width="1.8">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
                </svg>
            </div>

            <h1 class="card-title">Reset your password</h1>

            <hr class="divider" />

            <div class="card-body">
                <p>Hello,</p>
                <p>We received a request to reset the password for your <strong>Prolance</strong> workspace account. Click the button below to choose a new password.</p>
            </div>

            <div class="btn-container">
                <a href="${link}" class="btn">Reset Password</a>
            </div>

            <div class="warning">
                ⏱ This link will expire in <strong>${linkExpirationFormatter(linkExpiration)}</strong>. If you didn't request a password reset, you can safely ignore this email — your account is secure.
            </div>

            <div class="link-fallback">
                <p>If the button doesn't work, paste this link into your browser:</p>
                <a href="${link}">${link}</a>
            </div>

        </div>

        <!-- Footer -->
        <div class="footer">
            <p>© 2026 Prolance Inc. All rights reserved.</p>
            <p><a href="#">Privacy Policy</a> &nbsp;·&nbsp; <a href="#">Terms of Service</a></p>
        </div>

    </div>
</body>
</html>
