import { css } from "@emotion/css";

const IconWrapper = ({
  children,
  Icon,
  size = 22,
  color,
  hoverColor,
  onClick,
}: {
  children?: React.ReactNode;
  Icon: React.ElementType;
  size?: number;
  color?: string;
  hoverColor?: string;
  onClick?: () => void;
}) => {
  return (
    <>
      <Icon
        onClick={onClick}
        size={size}
        className={css`
          color: ${color} !important;
          cursor: pointer !important;

          
          ${hoverColor &&
          css`
            &:hover {
              color: ${hoverColor} !important;
            }
          `}
        `}
      />
      {children}
    </>
  );
};

export default IconWrapper;
