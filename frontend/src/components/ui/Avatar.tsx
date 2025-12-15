import { css, cx } from "@emotion/css";
import { LogOut } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { colorPalette } from "@/constants/colorPalette";
import { typography } from "@/constants/typography";
import { styles } from "@/constants/styles";
import CustomPopover from "@/components/ui/CustomPopover";

interface AvatarProps {
  email?: string;
  size?: number;
}

const avatarButtonStyle = (size: number, avatarColor: string) => css`
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 0;
  outline: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  width: ${size}px;
  height: ${size}px;
  background-color: ${avatarColor};

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.4);
  }

  &:active {
    transform: scale(0.98);
  }

  &:focus-visible {
    outline: 2px solid ${colorPalette.textActive};
    outline-offset: 2px;
  }
`;

const avatarLetterStyle = css`
  color: ${colorPalette.white};
  user-select: none;
  line-height: 1;
`;

const dropdownContentStyle = css`
  padding: 0;
`;

const dropdownHeaderStyle = css`
  padding: ${styles.padding.xs} ${styles.padding.small};
  border-radius: ${styles.borderRadius.small};
`;

const dropdownEmailStyle = css`
  color: ${colorPalette.text};
  word-break: break-all;
  margin-bottom: 4px;
`;

const dropdownRoleStyle = css`
  color: ${colorPalette.textMuted};
  text-transform: capitalize;
`;

const dropdownDividerStyle = css`
  height: 1px;
  background-color: ${colorPalette.strokePrimary};
  margin: 4px 0;
`;

const dropdownItemStyle = css`
  display: flex;
  align-items: center;
  gap: ${styles.padding.small};
  width: 100%;
  padding: ${styles.padding.xs} ${styles.padding.small};
  border: none;
  background: none;
  color: ${colorPalette.text};
  border-radius: ${styles.borderRadius.small};
  cursor: pointer;
  transition: all 0.15s ease;
  text-align: left;

  svg {
    flex-shrink: 0;
    color: ${colorPalette.textMuted};
    transition: color 0.15s ease;
  }

  &:hover {
    background-color: ${colorPalette.backgroundBright};
    color: ${colorPalette.textActive};

    svg {
      color: ${colorPalette.textActive};
    }
  }

  &:active {
    background-color: ${colorPalette.backgroundTertiary};
  }

  &:focus-visible {
    outline: 2px solid ${colorPalette.textActive};
    outline-offset: -2px;
  }
`;

export const Avatar = ({ email, size = 32 }: AvatarProps) => {
  const { user, logout } = useAuth();

  const displayEmail = email || user?.email || "";
  const firstLetter = displayEmail.charAt(0).toUpperCase();

  // Funkcja do generowania koloru na podstawie emaila
  const getAvatarColor = (email: string): string => {
    const colors = [
      "#C0504D", // dark red
      "#3AA89E", // dark teal
      "#4A90E2", // dark blue
      "#6B9F7D", // dark green
      "#8B7EDB", // dark purple
      "#D9864F", // dark orange
    ];

    let hash = 0;
    for (let i = 0; i < email.length; i++) {
      hash = email.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % colors.length;
    return colors[index];
  };

  const avatarColor = getAvatarColor(displayEmail);

  const handleLogout = async () => {
    await logout();
  };

  return (
    <CustomPopover
      width={240}
      position="bottom-end"
      trigger={
        <div
          className={avatarButtonStyle(size, avatarColor)}
          aria-label="User menu"
          role="button"
          tabIndex={0}
        >
          <span className={cx(avatarLetterStyle, typography.textM)}>
            {firstLetter}
          </span>
        </div>
      }
      content={
        <div className={dropdownContentStyle}>
          <div className={dropdownHeaderStyle}>
            <div className={cx(dropdownEmailStyle, typography.textM)}>
              {displayEmail}
            </div>
            {user?.role && (
              <div className={cx(dropdownRoleStyle, typography.textS)}>
                {user.role.toLowerCase()}
              </div>
            )}
          </div>

          <div className={dropdownDividerStyle} />

          <button
            className={cx(dropdownItemStyle, typography.textM)}
            onClick={handleLogout}
          >
            <LogOut size={16} />
            <span>Wyloguj się</span>
          </button>
        </div>
      }
    />
  );
};
