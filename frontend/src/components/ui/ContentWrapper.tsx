import { cx, css } from "@emotion/css";

const ContentWrapper = ({
  children,
  id,
  position,
  align,
  justify,
  padding,
  margin,
  width,
  maxWidth,
  minHeight,
  maxHeight,
  height,
  gap,
  direction,
  flex,
  flexValue,
  border,
  borderRadius,
  backgroundColor,
  wrap,
  customCss,
  onClick,
}: {
  children: React.ReactNode;
  id?: string;
  position?: string;
  align?: string;
  justify?: string;
  padding?: string;
  margin?: string;
  width?: string;
  minHeight?: string;
  height?: string;
  gap?: string;
  direction?: "row" | "column";
  maxWidth?: string;
  maxHeight?: string;
  flex?: true;
  flexValue?: string;
  backgroundColor?: string;
  border?: string;
  wrap?: string;
  borderRadius?: string;
  customCss?: string;
  onClick?: () => void;
}) => {
  return (
    <div
      onClick={onClick}
      id={id}
      className={cx(
        customCss,
        css`
          ${position ? `position: ${position};` : ""}
          ${align ? `align-items: ${align};` : ""}
             ${justify ? `justify-content: ${justify};` : ""}
             ${padding ? `padding: ${padding};` : ""}
             ${margin ? `margin: ${margin};` : ""}
             ${width ? `width: ${width};` : ""}
             ${maxWidth ? `max-width: ${maxWidth};` : ""}
             ${maxHeight ? `max-height: ${maxHeight};` : ""}
              ${minHeight ? `min-height: ${minHeight};` : ""}
             ${height ? `height: ${height};` : ""}
             ${gap ? `gap: ${gap};` : ""}
             ${direction ? `flex-direction: ${direction};` : ""}
             ${border ? `border: ${border};` : ""}
             ${borderRadius ? `border-radius: ${borderRadius};` : ""}
             ${backgroundColor ? `background-color: ${backgroundColor};` : ""}

             ${flexValue ? `flex: ${flexValue};` : ""}
             ${wrap ? `flex-wrap: ${wrap};` : ""}

             ${align || justify || flex || gap || direction
            ? `display: flex;`
            : ""}
        `
      )}
    >
      {children}
    </div>
  );
};

export default ContentWrapper;
